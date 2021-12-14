package us.mikeandwan.photos.domain

import androidx.room.withTransaction
import kotlinx.coroutines.flow.*
import us.mikeandwan.photos.api.ApiResult
import us.mikeandwan.photos.api.PhotoApiClient
import us.mikeandwan.photos.database.*
import us.mikeandwan.photos.domain.models.ExternalCallStatus
import us.mikeandwan.photos.domain.models.Photo
import us.mikeandwan.photos.ui.toExternalCallStatus
import javax.inject.Inject

class PhotoCategoryRepository @Inject constructor(
    private val api: PhotoApiClient,
    private val db: MawDatabase,
    private val pcDao: PhotoCategoryDao,
    private val idDao: ActiveIdDao
) {
    private var _lastCategoryId = -1
    private var _lastCategoryPhotos = emptyList<Photo>()

    fun getYears() = flow {
        val data = pcDao.getYears()

        if(data.first().isEmpty()) {
            emit(emptyList())
            loadCategories(-1).collect { }
        }

        emitAll(data)
    }

    suspend fun getNewCategories() = flow {
        val category = pcDao
            .getMostRecentCategory()
            .first()

        val categories = if (category == null) {
            loadCategories(-1)
        } else {
            loadCategories(category.id)
        }

        emitAll(categories)
    }

    fun getCategories() = pcDao
        .getCategoriesForActiveYear()
        .map { dbList ->
            dbList.map { dbCat -> dbCat.toDomainPhotoCategory() }
        }

    fun getCategory() = pcDao
        .getActiveCategory()
        .filter { it != null }
        .map { cat -> cat!!.toDomainPhotoCategory() }

    fun getCategory(id: Int) = pcDao
        .getCategory(id)
        .filter { it != null }
        .map { cat -> cat!!.toDomainPhotoCategory() }

    fun getPhotos(categoryId: Int) = flow {
        if(categoryId == _lastCategoryId) {
            emit(ExternalCallStatus.Success(_lastCategoryPhotos))
        } else {
            emit(ExternalCallStatus.Loading)

            when(val result = api.getPhotos(categoryId)) {
                is ApiResult.Error -> emit(result.toExternalCallStatus())
                is ApiResult.Empty -> emit(result.toExternalCallStatus())
                is ApiResult.Success -> {
                    _lastCategoryPhotos = result.result.items.map { it.toDomainPhoto() }

                    if (_lastCategoryPhotos.isNotEmpty()) {
                        _lastCategoryId = categoryId
                    }

                    emit(ExternalCallStatus.Success(_lastCategoryPhotos))
                }
            }
        }
    }

    private suspend fun loadCategories(mostRecentCategory: Int) = flow {
        emit(ExternalCallStatus.Loading)

        when(val result = api.getRecentCategories(mostRecentCategory)) {
            is ApiResult.Error -> emit(result.toExternalCallStatus())
            is ApiResult.Empty -> emit(result.toExternalCallStatus())
            is ApiResult.Success -> {
                val categories = result.result.items

                if(categories.isEmpty()) {
                    emit(ExternalCallStatus.Success(emptyList()))
                } else {
                    val dbCategories = categories.map { apiCat -> apiCat.toDatabasePhotoCategory() }
                    val maxYear = dbCategories.maxOf { it.year }

                    db.withTransaction {
                        pcDao.upsert(*dbCategories.toTypedArray())
                        idDao.setActiveId(ActiveId(ActiveIdType.PhotoCategoryYear, maxYear))
                    }

                    emit(ExternalCallStatus.Success(dbCategories.map { it.toDomainPhotoCategory() }))
                }
            }
        }
    }
}
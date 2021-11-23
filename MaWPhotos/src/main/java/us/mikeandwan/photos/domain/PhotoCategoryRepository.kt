package us.mikeandwan.photos.domain

import androidx.room.withTransaction
import kotlinx.coroutines.flow.*
import us.mikeandwan.photos.api.PhotoApiClient
import us.mikeandwan.photos.database.*
import us.mikeandwan.photos.domain.models.Photo
import us.mikeandwan.photos.domain.models.PhotoCategory
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
            loadCategories(-1)
        }

        emitAll(data)
    }

    fun getNewCategories() = pcDao
        .getMostRecentCategory()
        .map { if(it == null) loadCategories(-1) else loadCategories(it.id) }

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
        if(categoryId != _lastCategoryId) {
            val result = api.getPhotos(categoryId)

            _lastCategoryPhotos = result?.items?.map { it.toDomainPhoto() } ?: emptyList()

            if(_lastCategoryPhotos.isNotEmpty()) {
                _lastCategoryId = categoryId
            }
        }

        emit(_lastCategoryPhotos)
    }

    private suspend fun loadCategories(mostRecentCategory: Int): List<PhotoCategory> {
        val categories = api.getRecentCategories(mostRecentCategory)

        if(categories == null || categories.count == 0L) {
            return emptyList()
        }

        val dbCategories = categories.items
            .map { apiCat -> apiCat.toDatabasePhotoCategory() }

        val maxYear = dbCategories.maxOf { it.year }

        db.withTransaction {
            pcDao.upsert(*dbCategories.toTypedArray())
            idDao.setActiveId(ActiveId(ActiveIdType.PhotoCategoryYear, maxYear))
        }

        return dbCategories.map { it.toDomainPhotoCategory() }
    }
}
package us.mikeandwan.photos.domain

import androidx.room.withTransaction
import kotlinx.coroutines.flow.*
import us.mikeandwan.photos.api.ApiResult
import us.mikeandwan.photos.api.PhotoApiClient
import us.mikeandwan.photos.database.*
import us.mikeandwan.photos.domain.models.ExternalCallStatus
import us.mikeandwan.photos.domain.models.Photo
import javax.inject.Inject

class PhotoCategoryRepository @Inject constructor(
    private val api: PhotoApiClient,
    private val db: MawDatabase,
    private val pcDao: PhotoCategoryDao,
    private val apiErrorHandler: ApiErrorHandler
) : ICategoryRepository {
    companion object {
        const val ERR_MSG_LOAD_CATEGORIES = "Unable to load categories at this time.  Please try again later."
        const val ERR_MSG_LOAD_PHOTOS = "Unable to load photos.  Please try again later."
    }

    private var _lastCategoryId = -1
    private var _lastCategoryPhotos = emptyList<Photo>()

    override fun getYears() = flow {
        val data = pcDao.getYears()

        if(data.first().isEmpty()) {
            emit(emptyList())
            loadCategories(-1, ERR_MSG_LOAD_CATEGORIES)
                .collect { }
        }

        emitAll(data)
    }

    override fun getMostRecentYear() = pcDao.getMostRecentYear()

    override fun getNewCategories() = flow {
        val category = pcDao
            .getMostRecentCategory()
            .first()

        // do not show error messages as snackbar for this method as it is called only from
        // the update categories worker - which will create an error notification on failure
        val categories = if (category == null) {
            loadCategories(-1, null)
        } else {
            loadCategories(category.id, null)
        }

        emitAll(categories)
    }

    override fun getCategories(year: Int) = pcDao
        .getCategoriesForYear(year)
        .map { dbList ->
            dbList.map { dbCat -> dbCat.toDomainMediaCategory() }
        }

    override fun getCategory(categoryId: Int) = pcDao
        .getCategory(categoryId)
        .filter { it != null }
        .map { cat -> cat!!.toDomainMediaCategory() }

    override fun getMedia(categoryId: Int) = flow {
        if(categoryId == _lastCategoryId) {
            emit(ExternalCallStatus.Success(_lastCategoryPhotos))
        } else {
            emit(ExternalCallStatus.Loading)

            when(val result = api.getPhotos(categoryId)) {
                is ApiResult.Error -> emit(apiErrorHandler.handleError(result, ERR_MSG_LOAD_PHOTOS))
                is ApiResult.Empty -> emit(apiErrorHandler.handleEmpty(result, ERR_MSG_LOAD_PHOTOS))
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

    private fun loadCategories(mostRecentCategory: Int, errorMessage: String?) = flow {
        emit(ExternalCallStatus.Loading)

        when(val result = api.getRecentCategories(mostRecentCategory)) {
            is ApiResult.Error -> emit(apiErrorHandler.handleError(result, errorMessage))
            is ApiResult.Empty -> emit(apiErrorHandler.handleEmpty(result, errorMessage))
            is ApiResult.Success -> {
                val categories = result.result.items

                if(categories.isEmpty()) {
                    emit(ExternalCallStatus.Success(emptyList()))
                } else {
                    val dbCategories = categories.map { apiCat -> apiCat.toDatabasePhotoCategory() }

                    db.withTransaction {
                        pcDao.upsert(*dbCategories.toTypedArray())
                    }

                    emit(ExternalCallStatus.Success(dbCategories.map { it.toDomainMediaCategory() }))
                }
            }
        }
    }
}

package us.mikeandwan.photos.domain

import androidx.room.withTransaction
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import us.mikeandwan.photos.api.ApiResult
import us.mikeandwan.photos.api.PhotoApiClient
import us.mikeandwan.photos.authorization.AuthService
import us.mikeandwan.photos.database.*
import us.mikeandwan.photos.domain.models.ExternalCallStatus
import us.mikeandwan.photos.domain.models.Photo
import us.mikeandwan.photos.ui.toExternalCallStatus
import javax.inject.Inject

class PhotoCategoryRepository @Inject constructor(
    private val api: PhotoApiClient,
    private val db: MawDatabase,
    private val pcDao: PhotoCategoryDao,
    private val idDao: ActiveIdDao,
    private val errorRepository: ErrorRepository,
    private val authService: AuthService
) {
    companion object {
        const val ERR_MSG_LOAD_CATEGORIES = "Unable to load categories at this time.  Please try again later."
        const val ERR_MSG_LOAD_PHOTOS = "Unable to load photos.  Please try again later."
    }

    private var _lastCategoryId = -1
    private var _lastCategoryPhotos = emptyList<Photo>()

    fun getYears() = flow {
        val data = pcDao.getYears()

        if(data.first().isEmpty()) {
            emit(emptyList())
            loadCategories(-1, ERR_MSG_LOAD_CATEGORIES)
                .collect { }
        }

        emitAll(data)
    }

    fun getNewCategories() = flow {
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
                is ApiResult.Error -> emit(handleError(result, ERR_MSG_LOAD_PHOTOS))
                is ApiResult.Empty -> emit(handleEmpty(result, ERR_MSG_LOAD_PHOTOS))
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
            is ApiResult.Error -> emit(handleError(result, errorMessage))
            is ApiResult.Empty -> emit(handleEmpty(result, errorMessage))
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

    private fun handleError(error: ApiResult.Error, message: String?): ExternalCallStatus<Nothing> {
        if(error.exception is CancellationException) {
            return error.toExternalCallStatus()
        }

        if(error.isUnauthorized()) {
            authService.logout()
        } else {
            if(!message.isNullOrBlank()) {
                errorRepository.showError(message)
            }
        }

        return error.toExternalCallStatus()
    }

    private fun handleEmpty(empty: ApiResult.Empty, message: String?): ExternalCallStatus<Nothing> {
        if(!message.isNullOrBlank()) {
            errorRepository.showError(message)
        }

        return empty.toExternalCallStatus()
    }
}
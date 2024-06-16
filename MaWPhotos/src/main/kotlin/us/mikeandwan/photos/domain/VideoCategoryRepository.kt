package us.mikeandwan.photos.domain

import androidx.room.withTransaction
import kotlinx.coroutines.flow.*
import us.mikeandwan.photos.api.ApiResult
import us.mikeandwan.photos.api.VideoApiClient
import us.mikeandwan.photos.database.*
import us.mikeandwan.photos.domain.models.ExternalCallStatus
import us.mikeandwan.photos.domain.models.Video
import javax.inject.Inject

class VideoCategoryRepository @Inject constructor(
    private val api: VideoApiClient,
    private val db: MawDatabase,
    private val vcDao: VideoCategoryDao,
    private val apiErrorHandler: ApiErrorHandler
) {
    companion object {
        const val ERR_MSG_LOAD_CATEGORIES = "Unable to load categories at this time.  Please try again later."
        const val ERR_MSG_LOAD_VIDEOS = "Unable to load videos.  Please try again later."
    }

    private var _lastCategoryId = -1
    private var _lastCategoryVideos = emptyList<Video>()

    fun getYears() = flow {
        val data = vcDao.getYears()

        if(data.first().isEmpty()) {
            emit(emptyList())
            loadCategories(-1, ERR_MSG_LOAD_CATEGORIES)
                .collect { }
        }

        emitAll(data)
    }

    fun getMostRecentYear() = vcDao.getMostRecentYear()

    fun getNewCategories() = flow {
        val category = vcDao
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

    fun getCategories(year: Int) = vcDao
        .getCategoriesForYear(year)
        .map { dbList ->
            dbList.map { dbCat -> dbCat.toDomainMediaCategory() }
        }

    fun getCategory(id: Int) = vcDao
        .getCategory(id)
        .filter { it != null }
        .map { cat -> cat!!.toDomainMediaCategory() }

    fun getVideos(categoryId: Int) = flow {
        if(categoryId == _lastCategoryId) {
            emit(ExternalCallStatus.Success(_lastCategoryVideos))
        } else {
            emit(ExternalCallStatus.Loading)

            when(val result = api.getVideos(categoryId)) {
                is ApiResult.Error -> emit(apiErrorHandler.handleError(result, ERR_MSG_LOAD_VIDEOS))
                is ApiResult.Empty -> emit(apiErrorHandler.handleEmpty(result, ERR_MSG_LOAD_VIDEOS))
                is ApiResult.Success -> {
                    _lastCategoryVideos = result.result.items.map { it.toDomainVideo() }

                    if (_lastCategoryVideos.isNotEmpty()) {
                        _lastCategoryId = categoryId
                    }

                    emit(ExternalCallStatus.Success(_lastCategoryVideos))
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
                    val dbCategories = categories.map { apiCat -> apiCat.toDatabaseVideoCategory() }

                    db.withTransaction {
                        vcDao.upsert(*dbCategories.toTypedArray())
                    }

                    emit(ExternalCallStatus.Success(dbCategories.map { it.toDomainMediaCategory() }))
                }
            }
        }
    }
}

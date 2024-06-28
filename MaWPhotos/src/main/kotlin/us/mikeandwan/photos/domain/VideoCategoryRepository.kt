package us.mikeandwan.photos.domain

import androidx.collection.LruCache
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
) : ICategoryRepository {
    companion object {
        private const val ERR_MSG_LOAD_CATEGORIES = "Unable to load categories at this time.  Please try again later."
        private const val ERR_MSG_LOAD_VIDEOS = "Unable to load videos.  Please try again later."
    }

    private var cachedCategoryVideos = LruCache<Int, List<Video>>(8)

    override fun getYears() = flow {
        val data = vcDao.getYears()

        if(data.first().isEmpty()) {
            emit(emptyList())
            loadCategories(-1, ERR_MSG_LOAD_CATEGORIES)
                .collect { }
        }

        emitAll(data)
    }

    override fun getMostRecentYear() = vcDao.getMostRecentYear()

    override fun getNewCategories() = flow {
        val category = vcDao
            .getMostRecentCategory()
            .firstOrNull()

        // do not show error messages as snackbar for this method as it is called only from
        // the update categories worker - which will create an error notification on failure
        val categories = loadCategories(category?.id ?:-1, null)

        emitAll(categories)
    }

    override fun getCategories(year: Int) = vcDao
        .getCategoriesForYear(year)
        .map { dbList ->
            dbList.map { dbCat -> dbCat.toDomainMediaCategory() }
        }

    override fun getCategory(categoryId: Int) = vcDao
        .getCategory(categoryId)
        .filterNotNull()
        .map { cat -> cat.toDomainMediaCategory() }

    override fun getMedia(categoryId: Int) = flow {
        cachedCategoryVideos[categoryId]?.let {
            emit(ExternalCallStatus.Success(it))
            return@flow
        }

        emit(ExternalCallStatus.Loading)

        when(val result = api.getVideos(categoryId)) {
            is ApiResult.Error -> emit(apiErrorHandler.handleError(result, ERR_MSG_LOAD_VIDEOS))
            is ApiResult.Empty -> emit(apiErrorHandler.handleEmpty(result, ERR_MSG_LOAD_VIDEOS))
            is ApiResult.Success -> {
                val videos = result.result.items.map { it.toDomainVideo() }

                if (videos.isNotEmpty()) {
                    cachedCategoryVideos.put(categoryId, videos)
                }

                emit(ExternalCallStatus.Success(videos))
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

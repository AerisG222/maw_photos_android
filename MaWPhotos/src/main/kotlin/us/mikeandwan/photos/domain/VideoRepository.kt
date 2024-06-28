package us.mikeandwan.photos.domain

import kotlinx.coroutines.flow.flow
import us.mikeandwan.photos.api.ApiResult
import us.mikeandwan.photos.api.VideoApiClient
import us.mikeandwan.photos.domain.models.ExternalCallStatus
import javax.inject.Inject

class VideoRepository @Inject constructor (
    private val api: VideoApiClient,
    private val apiErrorHandler: ApiErrorHandler
){
    companion object {
        private const val ERR_MSG_LOAD_RATINGS = "Unable to load ratings at this time.  Please try again later."
        private const val ERR_MSG_LOAD_COMMENTS = "Unable to load comments at this time.  Please try again later."
        private const val ERR_MSG_ADD_COMMENTS = "Unable to add comments at this time.  Please try again later."
        private const val ERR_MSG_SET_RATING = "Unable to add ratings at this time.  Please try again later."
    }

    fun getRating(photoId: Int) = flow {
        emit(ExternalCallStatus.Loading)

        when(val result = api.getRatings(photoId)) {
            is ApiResult.Error -> emit(apiErrorHandler.handleError(result, ERR_MSG_LOAD_RATINGS))
            is ApiResult.Empty -> emit(apiErrorHandler.handleEmpty(result, ERR_MSG_LOAD_RATINGS))
            is ApiResult.Success -> emit(ExternalCallStatus.Success(result.result.toDomainRating()))
        }
    }

    fun getComments(photoId: Int) = flow {
        emit(ExternalCallStatus.Loading)

        when(val result = api.getComments(photoId)) {
            is ApiResult.Error -> emit(apiErrorHandler.handleError(result, ERR_MSG_LOAD_COMMENTS))
            is ApiResult.Empty -> emit(apiErrorHandler.handleEmpty(result, ERR_MSG_LOAD_COMMENTS))
            is ApiResult.Success -> emit(ExternalCallStatus.Success(result.result.items.map { it.toDomainComment() }))
        }
    }

    fun addComment(photoId: Int, comment: String) = flow {
        emit(ExternalCallStatus.Loading)

        when(val result = api.addComment(photoId, comment)) {
            is ApiResult.Error -> emit(apiErrorHandler.handleError(result, ERR_MSG_ADD_COMMENTS))
            is ApiResult.Empty -> emit(apiErrorHandler.handleEmpty(result, ERR_MSG_ADD_COMMENTS))
            is ApiResult.Success -> emit(ExternalCallStatus.Success(result.result.items.map{ it.toDomainComment() }))
        }
    }

    fun setRating(photoId: Int, rating: Short) = flow {
        emit(ExternalCallStatus.Loading)

        when(val result = api.setRating(photoId, rating)) {
            is ApiResult.Error -> emit(apiErrorHandler.handleError(result, ERR_MSG_SET_RATING))
            is ApiResult.Empty -> emit(apiErrorHandler.handleEmpty(result, ERR_MSG_SET_RATING))
            is ApiResult.Success -> emit(ExternalCallStatus.Success(result.result.toDomainRating()))
        }
    }
}

package us.mikeandwan.photos.domain

import kotlinx.coroutines.flow.flow
import us.mikeandwan.photos.api.ApiResult
import us.mikeandwan.photos.api.PhotoApiClient
import us.mikeandwan.photos.domain.models.ExternalCallStatus
import us.mikeandwan.photos.ui.toExternalCallStatus
import javax.inject.Inject

class PhotoRepository @Inject constructor (
    private val api: PhotoApiClient
){
    fun getExifData(photoId: Int) = flow {
        emit(ExternalCallStatus.Loading)

        when(val result = api.getExifData(photoId)) {
            is ApiResult.Error -> emit(result.toExternalCallStatus())
            is ApiResult.Empty -> emit(result.toExternalCallStatus())
            is ApiResult.Success -> emit(ExternalCallStatus.Success(result.result.toDomainExifData()))
        }
    }

    fun getRating(photoId: Int) = flow {
        emit(ExternalCallStatus.Loading)

        when(val result = api.getRatings(photoId)) {
            is ApiResult.Error -> emit(result.toExternalCallStatus())
            is ApiResult.Empty -> emit(result.toExternalCallStatus())
            is ApiResult.Success -> emit(ExternalCallStatus.Success(result.result.toDomainPhotoRating()))
        }
    }

    fun getComments(photoId: Int) = flow {
        emit(ExternalCallStatus.Loading)

        when(val result = api.getComments(photoId)) {
            is ApiResult.Error -> emit(result.toExternalCallStatus())
            is ApiResult.Empty -> emit(result.toExternalCallStatus())
            is ApiResult.Success -> emit(ExternalCallStatus.Success(result.result.items.map { it.toDomainPhotoComment() }))
        }
    }

    suspend fun addComment(photoId: Int, comment: String) = flow {
        emit(ExternalCallStatus.Loading)

        when(val result = api.addComment(photoId, comment)) {
            is ApiResult.Error -> emit(result.toExternalCallStatus())
            is ApiResult.Empty -> emit(result.toExternalCallStatus())
            is ApiResult.Success -> emit(ExternalCallStatus.Success(result.result.items.map{ it.toDomainPhotoComment() }))
        }
    }

    suspend fun setRating(photoId: Int, rating: Short) = flow {
        emit(ExternalCallStatus.Loading)

        when(val result = api.setRating(photoId, rating)) {
            is ApiResult.Error -> emit(result.toExternalCallStatus())
            is ApiResult.Empty -> emit(result.toExternalCallStatus())
            is ApiResult.Success -> emit(ExternalCallStatus.Success(result.result.toDomainPhotoRating()))
        }
    }
}
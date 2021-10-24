package us.mikeandwan.photos.domain

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import us.mikeandwan.photos.api.PhotoApiClient
import javax.inject.Inject

class PhotoRepository @Inject constructor (
    private val api: PhotoApiClient
){
    fun getExifData(photoId: Int) = flow {
        val result = api.getExifData(photoId)

        emit(result?.toDomainExifData())
    }

    fun getRating(photoId: Int) = flow {
        val result = api.getRatings(photoId)

        emit(result?.toDomainPhotoRating())
    }

    fun getComments(photoId: Int) = flow {
        val result = api.getComments(photoId)

        emit(result?.items?.map { it -> it.toDomainPhotoComment() })
    }

    suspend fun addComment(photoId: Int, comment: String): List<PhotoComment> {
        api.addComment(photoId, comment)

        return getComments(photoId).first() ?: emptyList()
    }

    suspend fun setRating(photoId: Int, rating: Short): Float? {
        return api.setRating(photoId, rating)
    }
}
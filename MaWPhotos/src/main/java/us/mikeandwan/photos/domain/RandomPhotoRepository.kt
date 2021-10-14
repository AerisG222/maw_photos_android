package us.mikeandwan.photos.domain

import us.mikeandwan.photos.api.PhotoApiClient
import javax.inject.Inject

class RandomPhotoRepository @Inject constructor(
    private val api: PhotoApiClient
) {
    suspend fun fetch(count: Int): List<Photo> {
        val result = api.getRandomPhotos(count)

        return if(result != null && result.items.isNotEmpty()) {
            result.items.map { it.toDomainPhoto() }
        } else {
            emptyList()
        }
    }
}
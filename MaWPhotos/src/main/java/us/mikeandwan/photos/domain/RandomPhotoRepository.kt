package us.mikeandwan.photos.domain

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import us.mikeandwan.photos.api.PhotoApiClient
import javax.inject.Inject

class RandomPhotoRepository @Inject constructor(
    private val api: PhotoApiClient
) {
    private val _photos = MutableStateFlow(emptyList<Photo>())
    val photos = _photos.asStateFlow()

    suspend fun fetch(count: Int) {
        val result = api.getRandomPhotos(count)

        if(result == null || result.items.isEmpty()) {
            return
        }

        val newPhotos = result.items.map { it.toDomainPhoto() }

        _photos.value = _photos.value + newPhotos
    }
}
package us.mikeandwan.photos.domain.services

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import us.mikeandwan.photos.domain.MediaRepository
import us.mikeandwan.photos.domain.models.ExternalCallStatus
import us.mikeandwan.photos.domain.models.Media
import us.mikeandwan.photos.utils.ExifDataFormatter.prepareForDisplay
import javax.inject.Inject

class MediaExifService @Inject constructor (
    private val mediaRepository: MediaRepository
) {
    private val _exif = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val exif = _exif.asStateFlow()

    suspend fun fetchExifDetails(media: Media) {
        mediaRepository.getExifData(media)
            .filter { it is ExternalCallStatus.Success }
            .map { it as ExternalCallStatus.Success }
            .map { prepareForDisplay(it.result) }
            .collect { _exif.value = it }
    }
}

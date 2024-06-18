package us.mikeandwan.photos.domain.services

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import us.mikeandwan.photos.domain.MediaRepository
import us.mikeandwan.photos.domain.models.ExternalCallStatus
import us.mikeandwan.photos.domain.models.Media
import javax.inject.Inject

class MediaRatingService @Inject constructor (
    private val mediaRepository: MediaRepository
){
    private val _userRating = MutableStateFlow<Short>(0)
    val userRating = _userRating.asStateFlow()

    private val _averageRating = MutableStateFlow(0f)
    val averageRating = _averageRating.asStateFlow()

    suspend fun setRating(media: Media, rating: Short) {
        val newAverageRating = mediaRepository.setRating(media, rating)

        newAverageRating
            .filter { it is ExternalCallStatus.Success }
            .map { it as ExternalCallStatus.Success }
            .collect {
                _userRating.value = rating
                _averageRating.value = it.result.averageRating
            }
    }

    suspend fun fetchRatingDetails(media: Media) {
        mediaRepository.getRating(media)
            .filter { it is ExternalCallStatus.Success }
            .map { it as ExternalCallStatus.Success }
            .collect {
                _userRating.value = it.result.userRating
                _averageRating.value = it.result.averageRating
            }
    }
}

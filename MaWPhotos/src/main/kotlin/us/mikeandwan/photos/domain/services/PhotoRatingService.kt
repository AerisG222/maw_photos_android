package us.mikeandwan.photos.domain.services

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import us.mikeandwan.photos.domain.PhotoRepository
import us.mikeandwan.photos.domain.models.ExternalCallStatus
import javax.inject.Inject

class PhotoRatingService @Inject constructor (
    private val photoRepository: PhotoRepository
){
    private val _userRating = MutableStateFlow<Short>(0)
    val userRating = _userRating.asStateFlow()

    private val _averageRating = MutableStateFlow(0f)
    val averageRating = _averageRating.asStateFlow()

    suspend fun setRating(photoId: Int, rating: Short) {
        val newAverageRating = photoRepository.setRating(photoId, rating)

        newAverageRating
            .filter { it is ExternalCallStatus.Success }
            .map { it as ExternalCallStatus.Success }
            .collect {
                _userRating.value = rating
                _averageRating.value = it.result.averageRating
            }
    }

    suspend fun fetchRatingDetails(photoId: Int) {
        photoRepository.getRating(photoId)
            .filter { it is ExternalCallStatus.Success }
            .map { it as ExternalCallStatus.Success }
            .collect {
                _userRating.value = it.result.userRating
                _averageRating.value = it.result.averageRating
            }
    }
}
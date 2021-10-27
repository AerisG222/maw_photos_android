package us.mikeandwan.photos.ui.controls.photorating

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import us.mikeandwan.photos.domain.ActiveIdRepository
import us.mikeandwan.photos.domain.PhotoRepository
import javax.inject.Inject

@HiltViewModel
class PhotoRatingViewModel @Inject constructor (
    private val activeIdRepository: ActiveIdRepository,
    private val photoRepository: PhotoRepository
): ViewModel() {
    private val _userRating = MutableStateFlow<Short>(0)
    val userRating = _userRating.asStateFlow()

    private val _averageRating = MutableStateFlow<Float>(0f)
    val averageRating = _averageRating.asStateFlow()
    
    suspend fun setRating(rating: Short) {
        val photoId = activeIdRepository.getActivePhotoId().first()

        if(photoId != null) {
            val newAverageRating = photoRepository.setRating(photoId, rating)

            if(newAverageRating == null) {
                _averageRating.value = 0f
            } else {
                _averageRating.value = newAverageRating
            }
        }
    }

    init {
        viewModelScope.launch {
            activeIdRepository
                .getActivePhotoId()
                .filter { it != null }
                .flatMapLatest { photoRepository.getRating(it!!) }
                .onEach {
                    _userRating.value = it!!.userRating
                    _averageRating.value = it!!.averageRating
                }
                .launchIn(this)
        }
    }
}
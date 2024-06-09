package us.mikeandwan.photos.ui.screens.random

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import us.mikeandwan.photos.domain.RandomPhotoRepository

abstract class BaseRandomViewModel (
    protected val randomPhotoRepository: RandomPhotoRepository
): ViewModel() {
    val photos = randomPhotoRepository
        .photos
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun fetch(count: Int) {
        viewModelScope.launch {
            randomPhotoRepository
                .fetch(count)
                .collect { }
        }
    }

    fun onResume() {
        randomPhotoRepository.setDoFetch(true)
    }

    fun onPause() {
        randomPhotoRepository.setDoFetch(false)
    }
}

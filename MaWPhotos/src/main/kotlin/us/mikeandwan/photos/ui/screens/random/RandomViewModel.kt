package us.mikeandwan.photos.ui.screens.random

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import us.mikeandwan.photos.domain.RandomPhotoRepository
import us.mikeandwan.photos.domain.RandomPreferenceRepository
import us.mikeandwan.photos.domain.models.GridThumbnailSize
import javax.inject.Inject

@HiltViewModel
class RandomViewModel @Inject constructor(
    private val randomPhotoRepository: RandomPhotoRepository,
    randomPreferenceRepository: RandomPreferenceRepository
) : ViewModel() {
    val photos = randomPhotoRepository
        .photos
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val gridItemThumbnailSize = randomPreferenceRepository
        .getPhotoGridItemSize()
        .stateIn(viewModelScope, SharingStarted.Eagerly, GridThumbnailSize.Medium)

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

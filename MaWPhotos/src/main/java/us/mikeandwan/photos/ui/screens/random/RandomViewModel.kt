package us.mikeandwan.photos.ui.screens.random

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import us.mikeandwan.photos.domain.*
import us.mikeandwan.photos.ui.controls.imagegrid.ImageGridRecyclerAdapter
import javax.inject.Inject

@HiltViewModel
class RandomViewModel @Inject constructor(
    private val randomPhotoRepository: RandomPhotoRepository,
    private val randomPreferenceRepository: RandomPreferenceRepository
) : ViewModel() {
    private val _activePhoto = MutableStateFlow<Photo?>(null)
    val activePhoto = _activePhoto.asStateFlow()

    val photos = randomPhotoRepository
        .photos
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList<Photo>())

    val gridItemThumbnailSize = randomPreferenceRepository
        .getPhotoGridItemSize()
        .stateIn(viewModelScope, SharingStarted.Eagerly, GridThumbnailSize.Medium)

    val onPhotoClicked = ImageGridRecyclerAdapter.ClickListener {
        Timber.i("photo clicked: ${it.id}")
        _activePhoto.value = it.data as Photo
    }

    private suspend fun performInitialFetch() {
        randomPhotoRepository.fetch(24)
    }

    fun onResume() {
        randomPhotoRepository.setDoFetch(true)
    }

    fun onPause() {
        randomPhotoRepository.setDoFetch(false)
    }

    init {
        viewModelScope.launch {
            if(randomPhotoRepository.photos.value.isEmpty()) {
                performInitialFetch()
            }

            randomPhotoRepository.setDoFetch(true)
        }
    }
}
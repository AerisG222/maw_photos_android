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
    private val activeIdRepository: ActiveIdRepository,
    private val randomPhotoRepository: RandomPhotoRepository,
    private val randomPreferenceRepository: RandomPreferenceRepository
) : ViewModel() {
    private val _requestNavigateToPhoto = MutableStateFlow<Int?>(null)
    val requestNavigateToPhoto = _requestNavigateToPhoto.asStateFlow()

    val photos = randomPhotoRepository
        .photos
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList<Photo>())

    val gridItemThumbnailSize = randomPreferenceRepository
        .getPhotoGridItemSize()
        .stateIn(viewModelScope, SharingStarted.Eagerly, GridThumbnailSize.Medium)

    val onPhotoClicked = ImageGridRecyclerAdapter.ClickListener {
        viewModelScope.launch {
            activeIdRepository.setActivePhoto(it.id)
            _requestNavigateToPhoto.value = it.id
        }
    }

    fun onNavigateComplete() {
        _requestNavigateToPhoto.value = null
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

    private suspend fun performInitialFetch() {
        randomPhotoRepository.fetch(24)
    }
}
package us.mikeandwan.photos.ui.screens.photo

import android.graphics.drawable.Drawable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import us.mikeandwan.photos.domain.ActiveIdRepository
import us.mikeandwan.photos.domain.FileStorageRepository
import us.mikeandwan.photos.domain.NavigationStateRepository
import us.mikeandwan.photos.domain.PhotoListMediator
import us.mikeandwan.photos.domain.models.NavigationArea
import us.mikeandwan.photos.domain.models.Photo
import us.mikeandwan.photos.domain.models.PhotoCategory
import java.io.File
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class PhotoViewModel @Inject constructor (
    private val activeIdRepository: ActiveIdRepository,
    photoListMediator: PhotoListMediator,
    private val fileStorageRepository: FileStorageRepository,
    private val navigationStateRepository: NavigationStateRepository,
): ViewModel() {
    val photos = photoListMediator.photos
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList<Photo>())

    val activePhotoIndex = photoListMediator.activePhotoIndex
        .filter { it >= 0 }
        .stateIn(viewModelScope, SharingStarted.Eagerly, -1)

    private val activePhoto = photoListMediator.activePhoto
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val activeCategory = photoListMediator.activeCategory
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val showYearAndCategory = navigationStateRepository.navArea
        .filter { it == NavigationArea.Random }
        .flatMapLatest { activeCategory }
        .map { it != null }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val showPosition = combine(activePhotoIndex, photos) { index, photos -> Pair(index, photos)}
        .map { (index, photos) -> index >= 0 && photos.isNotEmpty() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private val _pauseSlideshow = MutableStateFlow(false)

    private val _playSlideshow = MutableStateFlow(false)
    val playSlideshow = _playSlideshow.asStateFlow()

    private val _rotatePhoto = MutableStateFlow<Int>(0)
    val rotatePhoto = _rotatePhoto.asStateFlow()

    private val _sharePhoto = MutableStateFlow<Photo?>(null)
    val sharePhoto = _sharePhoto.asStateFlow()

    // this is used for its side effects - do not delete
    val slideshowTicker = combine(playSlideshow, _pauseSlideshow, photoListMediator.slideshowInterval) { play, pause, interval -> Triple(play, pause, interval) }
        .mapLatest { (play, pause, interval) ->
            while(true) {
                delay(interval * 1000L)

                if (play && !pause) {
                    gotoNextPhoto()
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, Unit)

    fun rotatePhoto(direction: Int) {
        _rotatePhoto.value = direction
    }

    fun rotateComplete() {
        _rotatePhoto.value = 0
    }

    fun sharePhoto() {
        _sharePhoto.value = activePhoto.value
    }

    fun sharePhotoComplete() {
        _sharePhoto.value = null
    }

    fun updateActivePhoto(index: Int) {
        val photo = photos.value[index]

        viewModelScope.launch {
            activeIdRepository.setActivePhoto(photo.id)
        }
    }

    fun navigateToYear(year: Int) {
        viewModelScope.launch {
            navigationStateRepository.requestNavigateToYear(year)
        }
    }

    fun navigateToCategory(category: PhotoCategory) {
        viewModelScope.launch {
            navigationStateRepository.requestNavigateToCategory(category)
        }
    }

    suspend fun savePhotoToShare(drawable: Drawable, originalFilename: String): File {
        return fileStorageRepository.savePhotoToShare(drawable, originalFilename)
    }

    fun toggleSlideshow() {
        _playSlideshow.update { !it }
    }

    fun pauseSlideshow() {
        _pauseSlideshow.value = true
    }

    fun unpauseSlideshow() {
        _pauseSlideshow.value = false
    }

    private fun gotoNextPhoto() {
        val idx = activePhotoIndex.value

        if(idx + 1 < photos.value.size) {
            updateActivePhoto(idx + 1)
        } else {
            _playSlideshow.value = false
        }
    }
}
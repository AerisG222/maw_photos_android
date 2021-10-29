package us.mikeandwan.photos.ui.screens.photo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import us.mikeandwan.photos.domain.*
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class PhotoViewModel @Inject constructor (
    private val activeIdRepository: ActiveIdRepository,
    private val photoListMediator: PhotoListMediator
): ViewModel() {
    val photos = photoListMediator.photos
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList<Photo>())

    val activePhotoIndex = photoListMediator.activePhotoIndex
        .filter { it >= 0 }
        .stateIn(viewModelScope, SharingStarted.Eagerly, -1)

    val activePhoto = photoListMediator.activePhoto
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val _rotatePhoto = MutableStateFlow<Int>(0)
    val rotatePhoto = _rotatePhoto.asStateFlow()

    private val _sharePhoto = MutableStateFlow<Photo?>(null)
    val sharePhoto = _sharePhoto.asStateFlow()

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
}
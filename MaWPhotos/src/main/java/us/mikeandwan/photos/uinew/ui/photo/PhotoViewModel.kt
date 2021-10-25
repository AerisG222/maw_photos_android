package us.mikeandwan.photos.uinew.ui.photo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import us.mikeandwan.photos.domain.*
import javax.inject.Inject

@HiltViewModel
class PhotoViewModel @Inject constructor (
    private val activeIdRepository: ActiveIdRepository
): ViewModel() {
    private val _photos = MutableStateFlow<List<Photo>>(emptyList())
    val photos = _photos.asStateFlow()

    private val _activePhotoIndex = MutableStateFlow(0)
    val activePhotoIndex = _activePhotoIndex.asStateFlow()

    private val _activePhoto = MutableStateFlow<Photo?>(null)
    val activePhoto = _activePhoto.asStateFlow()

    private val _rotatePhoto = MutableStateFlow<Int>(0)
    val rotatePhoto = _rotatePhoto.asStateFlow()

    fun rotatePhoto(direction: Int) {
        _rotatePhoto.value = direction
    }

    fun rotateComplete() {
        _rotatePhoto.value = 0
    }

    fun updatePhotoList(photoList: StateFlow<List<Photo>>, initialPhoto: Photo? = null) {
        _photos.value = photoList.value

        if(initialPhoto != null) {
            updateActivePhoto(initialPhoto)
        }
    }

    fun updateActivePhoto(index: Int) {
        val photo = _photos.value[index]

        _activePhotoIndex.value = index
        _activePhoto.value = photo

        viewModelScope.launch {
            activeIdRepository.setActivePhoto(photo.id)
        }
    }

    private fun updateActivePhoto(photo: Photo) {
        updateActivePhoto(photos.value.indexOf(photo))
    }
}
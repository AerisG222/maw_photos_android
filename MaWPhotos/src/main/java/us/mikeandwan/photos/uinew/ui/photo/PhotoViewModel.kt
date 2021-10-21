package us.mikeandwan.photos.uinew.ui.photo

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.*
import us.mikeandwan.photos.domain.*

class PhotoViewModel : ViewModel() {
    private val _photos = MutableStateFlow<List<Photo>>(emptyList())
    val photos = _photos.asStateFlow()

    private val _activePhotoIndex = MutableStateFlow(0)
    val activePhotoIndex = _activePhotoIndex.asStateFlow()

    private val _activePhoto = MutableStateFlow<Photo?>(null)
    val activePhoto = _activePhoto.asStateFlow()

    fun updatePhotoList(photoList: StateFlow<List<Photo>>, initialPhoto: Photo? = null) {
        _photos.value = photoList.value

        if(initialPhoto != null) {
            updateActivePhoto(initialPhoto)
        }
    }

    fun updateActivePhoto(index: Int) {
        _activePhotoIndex.value = index
        _activePhoto.value = _photos.value[index]
    }

    private fun updateActivePhoto(photo: Photo) {
        updateActivePhoto(photos.value.indexOf(photo))
    }
}
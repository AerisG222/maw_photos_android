package us.mikeandwan.photos.ui.screens.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import us.mikeandwan.photos.domain.PhotoCategoryRepository
import us.mikeandwan.photos.domain.PhotoPreferenceRepository
import us.mikeandwan.photos.domain.models.GridThumbnailSize
import us.mikeandwan.photos.domain.models.ExternalCallStatus
import us.mikeandwan.photos.domain.models.Photo
import us.mikeandwan.photos.domain.models.PhotoCategory
import us.mikeandwan.photos.ui.toImageGridItem
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor (
    private val photoCategoryRepository: PhotoCategoryRepository,
    photoPreferenceRepository: PhotoPreferenceRepository
) : ViewModel() {
    private val _category = MutableStateFlow<PhotoCategory?>(null)
    val category = _category.asStateFlow()

    private val _photos = MutableStateFlow<List<Photo>>(emptyList())
    val photos = _photos.asStateFlow()

    val gridItems = photos
        .map { photos -> photos.map { it.toImageGridItem() } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _activePhotoId = MutableStateFlow<Int>(-1)
    val activePhotoId = _activePhotoId.asStateFlow()

    val activePhotoIndex = photos.combine(activePhotoId) { photos, activePhotoId ->
        photos.indexOfFirst { it.id == activePhotoId }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, -1)

    val gridItemThumbnailSize = photoPreferenceRepository
        .getPhotoGridItemSize()
        .stateIn(viewModelScope, SharingStarted.Eagerly, GridThumbnailSize.Unspecified)

    fun loadCategory(categoryId: Int) {
        if(category.value?.id == categoryId) {
            return
        }

        viewModelScope.launch {
            photoCategoryRepository
                .getCategory(categoryId)
                .collect { _category.value = it }
        }
    }

    fun loadPhotos(categoryId: Int) {
        if(category.value?.id == categoryId) {
            return
        }

        viewModelScope.launch {
            photoCategoryRepository
                .getPhotos(categoryId)
                .filter { it is ExternalCallStatus.Success }
                .map { it as ExternalCallStatus.Success }
                .map { it.result }
                .collect { _photos.value = it }
        }
    }

    fun setActivePhotoId(photoId: Int) {
        _activePhotoId.value = photoId
    }
}

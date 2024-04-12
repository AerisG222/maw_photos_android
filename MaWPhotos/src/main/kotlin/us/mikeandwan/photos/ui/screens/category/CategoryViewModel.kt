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
import us.mikeandwan.photos.ui.controls.imagegrid.ImageGridItem
import us.mikeandwan.photos.ui.toImageGridItem
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor (
    private val photoCategoryRepository: PhotoCategoryRepository,
    photoPreferenceRepository: PhotoPreferenceRepository
) : ViewModel() {
    private val _photos = MutableStateFlow<List<ImageGridItem>>(emptyList())
    val photos = _photos.asStateFlow()

    val gridItemThumbnailSize = photoPreferenceRepository
        .getPhotoGridItemSize()
        .stateIn(viewModelScope, SharingStarted.Eagerly, GridThumbnailSize.Unspecified)

    fun loadCategory(categoryId: Int) {
        viewModelScope.launch {
            photoCategoryRepository
                .getPhotos(categoryId)
                .filter { it is ExternalCallStatus.Success }
                .map { it as ExternalCallStatus.Success }
                .map { it.result }
                .map { photos -> photos.map { it.toImageGridItem() } }
                .collect { _photos.value = it }
        }
    }
}
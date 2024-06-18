package us.mikeandwan.photos.ui.screens.category

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import us.mikeandwan.photos.domain.MediaCategoryRepository
import us.mikeandwan.photos.domain.PhotoPreferenceRepository
import us.mikeandwan.photos.domain.models.GridThumbnailSize
import us.mikeandwan.photos.ui.toImageGridItem
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor (
    mediaCategoryRepository: MediaCategoryRepository,
    photoPreferenceRepository: PhotoPreferenceRepository
) : BaseCategoryViewModel(
    mediaCategoryRepository
) {
    val gridItems = media
        .map { items -> items.map { it.toImageGridItem() } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val gridItemThumbnailSize = photoPreferenceRepository
        .getPhotoGridItemSize()
        .stateIn(viewModelScope, SharingStarted.Eagerly, GridThumbnailSize.Unspecified)
}

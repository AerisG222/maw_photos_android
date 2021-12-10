package us.mikeandwan.photos.ui.controls.imagegrid

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import us.mikeandwan.photos.domain.models.GridThumbnailSize
import us.mikeandwan.photos.ui.toImageGridItemWithSize

class ImageGridViewModel: ViewModel() {
    private val _gridItems = MutableStateFlow(emptyList<ImageGridItem>())
    private val _thumbnailSize = MutableStateFlow(0)

    val gridItemsWithSize = combine(
            _gridItems,
            _thumbnailSize
        ) { data, size -> Pair(data, size) }
        .filter { (_, size) -> size > 0 }
        .map { (data, size) -> data.map { it.toImageGridItemWithSize(size) }}
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _requestedThumbnailSize = MutableStateFlow(GridThumbnailSize.Unspecified)
    val requestedThumbnailSize = _requestedThumbnailSize.asStateFlow()

    fun setGridItems(items: List<ImageGridItem>) {
        _gridItems.value = items
    }

    fun setThumbnailSize(thumbnailSize: Int) {
        _thumbnailSize.value = thumbnailSize
    }

    fun setRequestedThumbnailSize(thumbnailSize: GridThumbnailSize) {
        _requestedThumbnailSize.value = thumbnailSize
    }
}
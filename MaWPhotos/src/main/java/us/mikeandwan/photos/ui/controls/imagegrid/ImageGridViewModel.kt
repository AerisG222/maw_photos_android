package us.mikeandwan.photos.ui.controls.imagegrid

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import us.mikeandwan.photos.domain.models.GridThumbnailSize
import us.mikeandwan.photos.ui.toImageGridItemWithSize

class ImageGridViewModel: ViewModel() {
    private val _items = MutableStateFlow(emptyList<ImageGridItem>())
    private val _thumbnailSize = MutableStateFlow(0)

    val gridItems = _items
        .combine(_thumbnailSize) { data, size -> Pair(data, size) }
        .filter { (data, size) -> data.isNotEmpty() && size > 0 }
        .map { (data, size) -> data.map { it.toImageGridItemWithSize(size) }}
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList<ImageGridItemWithSize>())

    private val _requestedThumbnailSize = MutableStateFlow<GridThumbnailSize?>(null)
    val requestedThumbnailSize = _requestedThumbnailSize.asStateFlow()

    fun setData(items: List<ImageGridItem>) {
        _items.value = items
    }

    fun setThumbnailSize(thumbnailSize: Int) {
        _thumbnailSize.value = thumbnailSize
    }

    fun setRequestedThumbnailSize(thumbnailSize: GridThumbnailSize) {
        _requestedThumbnailSize.value = thumbnailSize
    }
}
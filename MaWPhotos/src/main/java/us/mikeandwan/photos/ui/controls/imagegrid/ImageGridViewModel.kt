package us.mikeandwan.photos.ui.controls.imagegrid

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import us.mikeandwan.photos.domain.models.GridThumbnailSize

class ImageGridViewModel: ViewModel() {
    private val _gridItems = MutableStateFlow(emptyList<ImageGridItem>())
    val gridItems = _gridItems.asStateFlow()

    private val _thumbnailSize = MutableStateFlow(0)
    val thumbnailSize = _thumbnailSize.asStateFlow()

    // store the requested size here so it survives orientation change
    private val _requestedThumbnailSize = MutableStateFlow<GridThumbnailSize?>(null)
    val requestedThumbnailSize = _requestedThumbnailSize.asStateFlow()

    fun setData(items: List<ImageGridItem>) {
        _gridItems.value = items
    }

    fun setThumbnailSize(thumbnailSize: Int) {
        _thumbnailSize.value = thumbnailSize
    }

    fun setRequestedThumbnailSize(thumbnailSize: GridThumbnailSize) {
        _requestedThumbnailSize.value = thumbnailSize
    }
}
package us.mikeandwan.photos.uinew.ui.controls.imagegrid

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.*
import us.mikeandwan.photos.domain.GridThumbnailSize

class ImageGridViewModel: ViewModel() {
    private val _screenWidth = MutableStateFlow(0)
    val screenWidth = _screenWidth.asStateFlow()

    private val _gridItems = MutableStateFlow(emptyList<ImageGridItem>())
    val gridItems = _gridItems.asStateFlow()

    private val _doInvalidate = MutableStateFlow(false)
    val doInvalidate = _doInvalidate.asStateFlow()

    private val _thumbnailSize = MutableStateFlow(GridThumbnailSize.Medium)
    val thumbnailSize = _thumbnailSize.asStateFlow()

    fun setScreenWidth(width: Int) {
        _screenWidth.value = width
    }

    fun setData(items: List<ImageGridItem>) {
        _gridItems.value = items
    }

    fun setDoInvalidate(invalidate: Boolean) {
        _doInvalidate.value = invalidate
    }

    fun setThumbnailSize(thumbnailSize: GridThumbnailSize) {
        _thumbnailSize.value = thumbnailSize
    }
}
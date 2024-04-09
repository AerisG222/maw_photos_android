package us.mikeandwan.photos.ui.controls.imagegrid

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.*
import us.mikeandwan.photos.domain.models.CategoryRefreshStatus
import us.mikeandwan.photos.domain.models.GridThumbnailSize

class ImageGridViewModel: ViewModel() {
    private val _gridItems = MutableStateFlow(emptyList<ImageGridItem>())
    private val _thumbnailSize = MutableStateFlow(GridThumbnailSize.Unspecified)

    private val _refreshStatus = MutableStateFlow(CategoryRefreshStatus(false, null))
    val refreshStatus = _refreshStatus.asStateFlow()

    val gridItems = _gridItems.asStateFlow()
    val thumbnailSize = _thumbnailSize.asStateFlow()

    fun setGridItems(items: List<ImageGridItem>) {
        _gridItems.value = items
    }

    fun setThumbnailSize(thumbnailSize: GridThumbnailSize) {
        _thumbnailSize.value = thumbnailSize
    }

    fun setRefreshStatus(refreshStatus: CategoryRefreshStatus) {
        _refreshStatus.value = refreshStatus
    }
}
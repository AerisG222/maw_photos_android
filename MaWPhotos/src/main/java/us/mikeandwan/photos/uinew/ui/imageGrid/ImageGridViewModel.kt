package us.mikeandwan.photos.uinew.ui.imageGrid

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.*

class ImageGridViewModel: ViewModel() {
    private var _gridItems = MutableStateFlow(emptyList<ImageGridItem>())
    var gridItems = _gridItems.asStateFlow()

    private var _doInvalidate = MutableStateFlow(false)
    var doInvalidate = _doInvalidate.asStateFlow()

    fun setData(items: List<ImageGridItem>) {
        _gridItems.value = items
    }

    fun setDoInvalidate(invalidate: Boolean) {
        _doInvalidate.value = invalidate
    }
}
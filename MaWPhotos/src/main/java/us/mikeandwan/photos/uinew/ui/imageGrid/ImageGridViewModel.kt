package us.mikeandwan.photos.uinew.ui.imageGrid

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.*

class ImageGridViewModel: ViewModel() {
    private var _gridItems = MutableStateFlow(emptyList<ImageGridItem>())
    var gridItems = _gridItems.asStateFlow()

    fun setData(items: List<ImageGridItem>) {
        _gridItems.value = items
    }
}
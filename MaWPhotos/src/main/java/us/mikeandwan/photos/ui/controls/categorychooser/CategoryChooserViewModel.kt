package us.mikeandwan.photos.ui.controls.categorychooser

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import us.mikeandwan.photos.domain.models.CategoryDisplayType
import us.mikeandwan.photos.domain.models.GridThumbnailSize
import us.mikeandwan.photos.domain.models.PhotoCategory

class CategoryChooserViewModel: ViewModel() {
    private val _displayInfo = MutableStateFlow(CATEGORY_DISPLAY_INFO_DEFAULT)
    val displayInfo = _displayInfo.asStateFlow()

    fun setDisplayType(type: CategoryDisplayType) {
        _displayInfo.update { it.copy(displayType = type) }
    }

    fun setCategories(categories: List<PhotoCategory>) {
        _displayInfo.update { it.copy(categories = categories) }
    }

    fun setGridThumbnailSize(size: GridThumbnailSize) {
        _displayInfo.update { it.copy(gridThumbnailSize = size) }
    }

    fun setShowYearsInList(doShow: Boolean) {
        _displayInfo.update { it.copy(showYearInList = doShow) }
    }
}
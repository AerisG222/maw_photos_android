package us.mikeandwan.photos.ui.controls.categorylist

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import us.mikeandwan.photos.domain.models.PhotoCategory

class CategoryListViewModel: ViewModel() {
    private var _categories = MutableStateFlow(emptyList<PhotoCategory>())
    var categories = _categories.asStateFlow()

    private var _doInvalidate = MutableStateFlow(false)
    var doInvalidate = _doInvalidate.asStateFlow()

    fun setCategories(items: List<PhotoCategory>) {
        _categories.value = items
    }

    fun setDoInvalidate(invalidate: Boolean) {
        _doInvalidate.value = invalidate
    }
}
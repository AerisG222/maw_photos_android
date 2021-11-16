package us.mikeandwan.photos.ui.controls.categorylist

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import us.mikeandwan.photos.domain.models.PhotoCategory

class CategoryListViewModel: ViewModel() {
    private var _categories = MutableStateFlow(emptyList<PhotoCategory>())
    var categories = _categories.asStateFlow()

    fun setCategories(items: List<PhotoCategory>) {
        _categories.value = items
    }
}
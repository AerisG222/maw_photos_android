package us.mikeandwan.photos.ui.controls.categorylist

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import us.mikeandwan.photos.domain.models.CategoryRefreshStatus
import us.mikeandwan.photos.domain.models.PhotoCategory
import us.mikeandwan.photos.ui.toCategoryWithYearVisibility

class CategoryListViewModel: ViewModel() {
    private val _categories = MutableStateFlow(emptyList<CategoryWithYearVisibility>())
    val categories = _categories.asStateFlow()

    private val _refreshStatus = MutableStateFlow(CategoryRefreshStatus(false, null))
    val refreshStatus = _refreshStatus.asStateFlow();
    
    private val _showYear = MutableStateFlow(false)

    fun setCategories(items: List<PhotoCategory>) {
        _categories.value = items.map { it.toCategoryWithYearVisibility(_showYear.value) }
    }

    fun setShowYear(doShow: Boolean) {
        _showYear.value = doShow
    }

    fun setRefreshStatus(refreshStatus: CategoryRefreshStatus) {
        _refreshStatus.value = refreshStatus
    }
}
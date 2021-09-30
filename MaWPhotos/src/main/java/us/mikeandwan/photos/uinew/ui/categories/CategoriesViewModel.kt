package us.mikeandwan.photos.uinew.ui.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import us.mikeandwan.photos.domain.PhotoCategory
import us.mikeandwan.photos.domain.PhotoCategoryRepository
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor (
    private val photoCategoryRepository: PhotoCategoryRepository
): ViewModel() {
    private val _categories = MutableStateFlow<List<PhotoCategory>>(emptyList())
    val categories: StateFlow<List<PhotoCategory>> = _categories

    init {
        viewModelScope.launch {
            photoCategoryRepository
                .getCategories()
                .collect {
                    _categories.value = it
                }
        }
    }
}
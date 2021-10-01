package us.mikeandwan.photos.uinew.ui.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import timber.log.Timber
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
        photoCategoryRepository
            .getCategories()
            .onEach { result ->
                _categories.value = result
            }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }

    fun onCategorySelected(photoCategory: PhotoCategory) {
        Timber.i("Category selected ${photoCategory.name}")
    }
}
package us.mikeandwan.photos.uinew.ui.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import us.mikeandwan.photos.domain.*
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor (
    private val photoCategoryRepository: PhotoCategoryRepository,
    private val categoryPreferenceRepository: CategoryPreferenceRepository,
    private val activeIdRepository: ActiveIdRepository,
    private val navigationStateRepository: NavigationStateRepository
): ViewModel() {
    private val _categories = MutableStateFlow<List<PhotoCategory>>(emptyList())
    val categories = _categories.asStateFlow()

    private val _displayType = MutableStateFlow(CategoryDisplayType.Grid)
    val displayType = _displayType.asStateFlow()

    init {
        viewModelScope.launch {
            categoryPreferenceRepository
                .getCategoryDisplayType()
                .collect { type ->
                    _displayType.value = type
                }
        }

        viewModelScope.launch {
            photoCategoryRepository
                .getCategories()
                .collect { result ->
                    _categories.value = result
                }
        }
    }

    fun onCategorySelected(photoCategory: PhotoCategory) {
        viewModelScope.launch {
            activeIdRepository.setActivePhotoCategory(photoCategory.id)
        }
    }
}
package us.mikeandwan.photos.ui.screens.categories

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
    private val activeIdRepository: ActiveIdRepository
): ViewModel() {
    private val _categories = MutableStateFlow<List<PhotoCategory>>(emptyList())
    val categories = _categories.asStateFlow()

    private val _preferences = MutableStateFlow(CATEGORY_PREFERENCE_DEFAULT)
    val preferences = _preferences.asStateFlow()

    init {
        viewModelScope.launch {
            categoryPreferenceRepository
                .getCategoryPreference()
                .onEach { _preferences.value = it }
                .launchIn(this)

            photoCategoryRepository
                .getCategories()
                .onEach { _categories.value = it }
                .launchIn(this)
        }
    }

    fun onCategorySelected(categoryId: Int) {
        viewModelScope.launch {
            activeIdRepository.setActivePhotoCategory(categoryId)
        }
    }
}
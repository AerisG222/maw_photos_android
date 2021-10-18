package us.mikeandwan.photos.uinew.ui.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import us.mikeandwan.photos.domain.*
import us.mikeandwan.photos.uinew.ui.imageGrid.ImageGridItem
import us.mikeandwan.photos.uinew.ui.toImageGridItem
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
                .onEach { _displayType.value = it }
                .launchIn(this)

            photoCategoryRepository
                .getCategories()
                .onEach { _categories.value = it }
                .launchIn(this)

            activeIdRepository
                .getActivePhotoCategoryYear()
                .filter { it != null }
                .onEach {
                    navigationStateRepository.requestNavDrawerClose()
                    navigationStateRepository.setToolbarTitle(it.toString())
                }
                .launchIn(this)
        }
    }

    fun onCategorySelected(categoryId: Int) {
        viewModelScope.launch {
            activeIdRepository.setActivePhotoCategory(categoryId)
        }
    }
}
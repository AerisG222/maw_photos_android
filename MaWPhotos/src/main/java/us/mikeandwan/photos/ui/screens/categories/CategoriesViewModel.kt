package us.mikeandwan.photos.ui.screens.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import us.mikeandwan.photos.domain.ActiveIdRepository
import us.mikeandwan.photos.domain.CategoryPreferenceRepository
import us.mikeandwan.photos.domain.NavigationStateRepository
import us.mikeandwan.photos.domain.PhotoCategoryRepository
import us.mikeandwan.photos.domain.models.CATEGORY_PREFERENCE_DEFAULT
import us.mikeandwan.photos.ui.controls.categorychooser.CategoryChooserFragment
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class CategoriesViewModel @Inject constructor (
    photoCategoryRepository: PhotoCategoryRepository,
    categoryPreferenceRepository: CategoryPreferenceRepository,
    private val activeIdRepository: ActiveIdRepository,
    private val navigationStateRepository: NavigationStateRepository
): ViewModel() {
    private val _requestNavigateToCategory = MutableStateFlow<Int?>(null)
    val requestNavigateToCategory = _requestNavigateToCategory.asStateFlow()

    val categories = photoCategoryRepository
        .getCategories()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val preferences = categoryPreferenceRepository
        .getCategoryPreference()
        .stateIn(viewModelScope, SharingStarted.Eagerly, CATEGORY_PREFERENCE_DEFAULT)

    // used for its side effect - do not delete
    val updateTitlebar = activeIdRepository
        .getActivePhotoCategoryYear()
        .distinctUntilChanged()
        .mapLatest { navigationStateRepository.overrideTitle(it.toString()) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "")

    val onCategorySelected = CategoryChooserFragment.CategorySelectedListener {
        viewModelScope.launch {
            activeIdRepository.setActivePhotoCategory(it.id)
            _requestNavigateToCategory.value = it.id
        }
    }

    fun onNavigateComplete() {
        _requestNavigateToCategory.value = null
    }
}
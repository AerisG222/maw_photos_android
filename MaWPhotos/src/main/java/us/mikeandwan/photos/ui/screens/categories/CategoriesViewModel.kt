package us.mikeandwan.photos.ui.screens.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import us.mikeandwan.photos.domain.ActiveIdRepository
import us.mikeandwan.photos.domain.CategoryPreferenceRepository
import us.mikeandwan.photos.domain.NavigationStateRepository
import us.mikeandwan.photos.domain.PhotoCategoryRepository
import us.mikeandwan.photos.domain.models.CATEGORY_PREFERENCE_DEFAULT
import us.mikeandwan.photos.domain.models.PhotoCategory
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class CategoriesViewModel @Inject constructor (
    private val photoCategoryRepository: PhotoCategoryRepository,
    private val categoryPreferenceRepository: CategoryPreferenceRepository,
    private val activeIdRepository: ActiveIdRepository,
    private val navigationStateRepository: NavigationStateRepository
): ViewModel() {
    val categories = photoCategoryRepository
        .getCategories()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList<PhotoCategory>())

    val preferences = categoryPreferenceRepository
        .getCategoryPreference()
        .stateIn(viewModelScope, SharingStarted.Eagerly, CATEGORY_PREFERENCE_DEFAULT)

    // used for its side effect - do not delete
    val updateTitlebar = activeIdRepository
        .getActivePhotoCategoryYear()
        .mapLatest { navigationStateRepository.overrideTitle(it.toString()) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "")

    fun onCategorySelected(categoryId: Int) {
        viewModelScope.launch {
            activeIdRepository.setActivePhotoCategory(categoryId)
        }
    }
}
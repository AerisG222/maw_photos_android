package us.mikeandwan.photos.ui.screens.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import us.mikeandwan.photos.domain.CategoryPreferenceRepository
import us.mikeandwan.photos.domain.PhotoCategoryRepository
import us.mikeandwan.photos.domain.models.CATEGORY_PREFERENCE_DEFAULT
import us.mikeandwan.photos.domain.models.CategoryRefreshStatus
import us.mikeandwan.photos.domain.models.ExternalCallStatus
import us.mikeandwan.photos.domain.models.PhotoCategory
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor (
    private val photoCategoryRepository: PhotoCategoryRepository,
    categoryPreferenceRepository: CategoryPreferenceRepository
): ViewModel() {
    private val _refreshStatus = MutableStateFlow(CategoryRefreshStatus(0, false, null))
    val refreshStatus = _refreshStatus.asStateFlow()

    private val _categories = MutableStateFlow<List<PhotoCategory>>(emptyList())
    val categories = _categories.asStateFlow()

    val preferences = categoryPreferenceRepository
        .getCategoryPreference()
        .stateIn(viewModelScope, SharingStarted.Eagerly, CATEGORY_PREFERENCE_DEFAULT)

    fun onRefreshCategories(id: Int) {
        viewModelScope.launch {
            photoCategoryRepository
                .getNewCategories()
                .onEach {
                    when(it) {
                        is ExternalCallStatus.Loading -> {
                            _refreshStatus.value = CategoryRefreshStatus(id,true, null)
                        }
                        is ExternalCallStatus.Success -> {
                            val msg = when(it.result.count()) {
                                0 -> "No new categories available"
                                1 -> "One new category loaded"
                                else -> "${it.result.count()} categories loaded"
                            }

                            _refreshStatus.value = CategoryRefreshStatus(id,false, msg)
                        }
                        is ExternalCallStatus.Error -> {
                            _refreshStatus.value = CategoryRefreshStatus(id,false, "There was an error loading categories")
                        }
                    }
                }
                .catch { e -> Timber.e(e) }
                .launchIn(this)
        }
    }

    fun loadCategories(year: Int) {
        viewModelScope.launch {
            photoCategoryRepository
                .getCategories(year)
                .collect { cats -> _categories.value = cats }
        }
    }
}

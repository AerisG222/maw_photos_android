package us.mikeandwan.photos.ui.screens.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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
    private val _refreshStatus = MutableStateFlow(CategoryRefreshStatus(false, null))
    val refreshStatus = _refreshStatus.asStateFlow()

    private val _categories = MutableStateFlow<List<PhotoCategory>>(emptyList())
    val categories = _categories.asStateFlow()

    val preferences = categoryPreferenceRepository
        .getCategoryPreference()
        .stateIn(viewModelScope, SharingStarted.Eagerly, CATEGORY_PREFERENCE_DEFAULT)

    // TODO: 1 did not work on emulator, 10 did work, lets make it 20 to be safe - FUGLY!
    private val HACK_DELAY = 20L

    val onRefreshCategories: () -> Unit = {
        viewModelScope.launch {
            photoCategoryRepository
                .getNewCategories()
                .onEach {
                    when(it) {
                        is ExternalCallStatus.Loading -> {
                            _refreshStatus.value = CategoryRefreshStatus(true, null)

                            // TODO: this delay ensures that observers will see the change of refresh status
                            delay(HACK_DELAY)
                        }
                        is ExternalCallStatus.Success -> {
                            val msg = when(it.result.count()) {
                                0 -> "No new categories available"
                                1 -> "One new category loaded"
                                else -> "${it.result.count()} categories loaded"
                            }

                            _refreshStatus.value = CategoryRefreshStatus(false, msg)

                            // TODO: this delay ensures that observers will see the change of refresh status
                            delay(HACK_DELAY)
                        }
                        is ExternalCallStatus.Error -> {
                            _refreshStatus.value = CategoryRefreshStatus(false, "There was an error loading categories")

                            // TODO: this delay ensures that observers will see the change of refresh status
                            delay(HACK_DELAY)
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
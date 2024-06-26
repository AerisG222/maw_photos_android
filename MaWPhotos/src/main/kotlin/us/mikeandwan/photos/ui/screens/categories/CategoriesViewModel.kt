package us.mikeandwan.photos.ui.screens.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.launch
import timber.log.Timber
import us.mikeandwan.photos.domain.CategoryPreferenceRepository
import us.mikeandwan.photos.domain.MediaCategoryRepository
import us.mikeandwan.photos.domain.guards.AuthGuard
import us.mikeandwan.photos.domain.guards.CategoriesLoadedGuard
import us.mikeandwan.photos.domain.guards.GuardStatus
import us.mikeandwan.photos.domain.models.CATEGORY_PREFERENCE_DEFAULT
import us.mikeandwan.photos.domain.models.CategoryRefreshStatus
import us.mikeandwan.photos.domain.models.ExternalCallStatus
import us.mikeandwan.photos.domain.models.MediaCategory
import javax.inject.Inject
import com.hoc081098.flowext.combine
import us.mikeandwan.photos.domain.models.CategoryPreference
import kotlin.random.Random

sealed class CategoriesState {
    data object Unknown : CategoriesState()
    data object NotAuthorized : CategoriesState()
    data class InvalidYear(val mostRecentYear: Int) : CategoriesState()
    data object Error : CategoriesState()
    data class Valid(
        val categories: List<MediaCategory>,
        val refreshStatus: CategoryRefreshStatus,
        val preferences: CategoryPreference,
        val refreshCategories: () -> Unit
    ) : CategoriesState()
}

@HiltViewModel
class CategoriesViewModel @Inject constructor (
    private val mediaCategoryRepository: MediaCategoryRepository,
    authGuard: AuthGuard,
    categoriesLoadedGuard: CategoriesLoadedGuard,
    categoryPreferenceRepository: CategoryPreferenceRepository
): ViewModel() {
    private val _year = MutableStateFlow(-1)
    private val _categories = MutableStateFlow<List<MediaCategory>>(emptyList())
    private val _refreshStatus = MutableStateFlow(CategoryRefreshStatus(0, false, null))
    private val _preferences = categoryPreferenceRepository
        .getCategoryPreference()
        .stateIn(viewModelScope, WhileSubscribed(5000), CATEGORY_PREFERENCE_DEFAULT)

    fun setYear(year: Int) {
        _year.value = year
    }

    private var isFetchingCategories = false

    val state = combine(
        authGuard.status,
        categoriesLoadedGuard.status,
        mediaCategoryRepository.getYears(),
        _categories,
        _year,
        _refreshStatus,
        _preferences
    ) { authStatus,
        categoriesStatus,
        years,
        categories,
        year,
        refreshStatus,
        preferences ->

        if (year <= 0) {
            return@combine CategoriesState.Unknown
        }

        when(authStatus) {
            is GuardStatus.Failed -> CategoriesState.NotAuthorized
            is GuardStatus.Passed -> {
                when(categoriesStatus) {
                    is GuardStatus.Failed -> CategoriesState.Error
                    is GuardStatus.Passed ->
                        when {
                            years.isEmpty() -> CategoriesState.Unknown
                            !years.contains(year) -> CategoriesState.InvalidYear(years.max())
                            categories.isEmpty() -> {
                                if (!isFetchingCategories) {
                                    isFetchingCategories = true
                                    loadCategories(year)
                                }
                                CategoriesState.Unknown
                            }
                            else -> CategoriesState.Valid(
                                    categories,
                                    refreshStatus,
                                    preferences,
                                    refreshCategories = { refreshCategories(Random.nextInt()) }
                                )
                        }
                    else -> CategoriesState.Unknown
                }
            }
            else -> CategoriesState.Unknown
        }
    }
    .stateIn(viewModelScope, WhileSubscribed(5000), CategoriesState.Unknown)

    private fun refreshCategories(id: Int) {
        viewModelScope.launch {
            mediaCategoryRepository
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

    private fun loadCategories(year: Int) {
        viewModelScope.launch {
            mediaCategoryRepository
                .getCategories(year)
                .collect { cats -> _categories.value = cats }
        }
    }
}

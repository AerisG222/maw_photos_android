package us.mikeandwan.photos.ui.controls.yearnavmenu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import us.mikeandwan.photos.domain.ActiveIdRepository
import us.mikeandwan.photos.domain.NavigationStateRepository
import us.mikeandwan.photos.domain.PhotoCategoryRepository
import javax.inject.Inject

@HiltViewModel
class YearsViewModel @Inject constructor (
    photoCategoryRepository: PhotoCategoryRepository,
    activeIdRepository: ActiveIdRepository,
    private val navigationStateRepository: NavigationStateRepository
): ViewModel() {
    val years = photoCategoryRepository
        .getYears()
        .filter { it.isNotEmpty() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val activeYear = activeIdRepository
        .getActivePhotoCategoryYear()
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun onYearSelected(year: Int) {
        viewModelScope.launch {
            navigationStateRepository.requestNavigateToYear(year)
        }
    }
}
package us.mikeandwan.photos.uinew.ui.yearnavmenu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import us.mikeandwan.photos.R
import us.mikeandwan.photos.domain.ActiveIdRepository
import us.mikeandwan.photos.domain.NavigationStateRepository
import us.mikeandwan.photos.domain.PhotoCategoryRepository
import javax.inject.Inject

@HiltViewModel
class YearsViewModel @Inject constructor (
    private val photoCategoryRepository: PhotoCategoryRepository,
    private val activeIdRepository: ActiveIdRepository,
    private val navigationStateRepository: NavigationStateRepository
): ViewModel() {
    private val _years = MutableStateFlow<List<Int>>(emptyList())
    val years = _years.asStateFlow()

    private val _activeYear = MutableStateFlow<Int?>(null)
    val activeYear = _activeYear.asStateFlow()

    init {
        photoCategoryRepository
            .getYears()
            .filter { it.isNotEmpty() }
            .onEach { _years.value = it }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)

        activeIdRepository
            .getActivePhotoCategoryYear()
            .onEach { _activeYear.value = it }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }

    fun onYearSelected(year: Int) {
        viewModelScope.launch {
            activeIdRepository.setActivePhotoCategoryYear(year)
            navigationStateRepository.requestNavigation(R.id.navigation_categories)
        }
    }
}
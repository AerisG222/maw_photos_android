package us.mikeandwan.photos.uinew.ui.years

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import us.mikeandwan.photos.domain.ActiveIdRepository
import us.mikeandwan.photos.domain.PhotoCategoryRepository
import javax.inject.Inject

@HiltViewModel
class YearsViewModel @Inject constructor (
    private val photoCategoryRepository: PhotoCategoryRepository,
    private val activeIdRepository: ActiveIdRepository
): ViewModel() {
    private val _years = MutableStateFlow<List<Int>>(emptyList())
    val years = _years.asStateFlow()

    private val _activeYear = MutableStateFlow<Int>(0)
    val activeYear = _activeYear.asStateFlow()

    init {
        photoCategoryRepository
            .getYears()
            .onEach { yearList ->
                _years.value = yearList
            }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)

        activeIdRepository
            .getActivePhotoCategoryYear()
            .onEach { year ->
                _activeYear.value = year
            }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }

    fun onYearSelected(year: Int) {
        viewModelScope.launch {
            activeIdRepository.setActivePhotoCategoryYear(year)
        }
    }
}
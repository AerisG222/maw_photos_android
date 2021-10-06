package us.mikeandwan.photos.uinew.ui.years

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import us.mikeandwan.photos.domain.ActiveIdRepository
import us.mikeandwan.photos.domain.PhotoCategoryRepository
import javax.inject.Inject

@HiltViewModel
class YearsViewModel @Inject constructor (
    private val photoCategoryRepository: PhotoCategoryRepository,
    private val activeIdRepository: ActiveIdRepository
): ViewModel() {
    private val _years = MutableStateFlow<List<Int>>(emptyList())
    val years: StateFlow<List<Int>> = _years

    init {
        photoCategoryRepository
            .getYears()
            .onEach { result ->
                _years.value = result
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
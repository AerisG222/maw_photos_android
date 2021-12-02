package us.mikeandwan.photos.ui.controls.searchnavmenu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import us.mikeandwan.photos.domain.NavigationStateRepository
import us.mikeandwan.photos.domain.SearchRepository
import javax.inject.Inject

@HiltViewModel
class SearchNavMenuViewModel @Inject constructor (
    private val searchRepository: SearchRepository,
    private val navigationStateRepository: NavigationStateRepository
): ViewModel() {
    val searchTerms = searchRepository
        .getSearchHistory()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun onTermSelected(term: String) {
        viewModelScope.launch {
            //navigationStateRepository.requestNavigateToYear(year)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            searchRepository.clearHistory()
        }
    }
}
package us.mikeandwan.photos.ui.controls.toolbar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import us.mikeandwan.photos.domain.NavigationStateRepository
import us.mikeandwan.photos.domain.SearchRepository
import us.mikeandwan.photos.domain.models.NavigationArea
import us.mikeandwan.photos.domain.models.SearchRequest
import us.mikeandwan.photos.domain.models.SearchSource
import javax.inject.Inject

@HiltViewModel
class ToolbarViewModel @Inject constructor(
    private val navigationStateRepository: NavigationStateRepository,
    private val searchRepository: SearchRepository,
): ViewModel() {
    val showAppIcon = navigationStateRepository.enableDrawer
    val toolbarTitle = navigationStateRepository.toolbarTitle

    private val _closeKeyboardSignal = MutableStateFlow(false)
    val closeKeyboardSignal = _closeKeyboardSignal.asStateFlow()

    val showSearch = navigationStateRepository
        .navArea
        .map { it == NavigationArea.Search }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val searchRequest = searchRepository
        .searchRequest
        .stateIn(viewModelScope, SharingStarted.Eagerly, SearchRequest("", SearchSource.None))

    fun onAppIconClicked() {
        navigationStateRepository.requestNavDrawerOpen()
    }

    fun onBackClicked() {
        navigationStateRepository.requestNavigateBack()
    }

    fun search(query: String) {
        _closeKeyboardSignal.value = true

        viewModelScope.launch {
            searchRepository
                .performSearch(query, SearchSource.QueryInterface)
                .collect { }
        }
    }

    fun closeKeyboardSignalHandled() {
        _closeKeyboardSignal.value = false
    }
}
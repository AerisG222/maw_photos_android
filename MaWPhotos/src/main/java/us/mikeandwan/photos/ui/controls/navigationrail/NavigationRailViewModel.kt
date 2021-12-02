package us.mikeandwan.photos.ui.controls.navigationrail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import us.mikeandwan.photos.domain.NavigationStateRepository
import us.mikeandwan.photos.domain.models.NavigationArea
import javax.inject.Inject

@HiltViewModel
class NavigationRailViewModel @Inject constructor(
    private val navigationStateRepository: NavigationStateRepository
): ViewModel() {
    private var _activeColor: Int = 0
    private var _inActiveColor: Int = 0

    private val _aboutButtonColor = MutableStateFlow(0)
    val aboutButtonColor = _aboutButtonColor.asStateFlow()

    private val _categoryButtonColor = MutableStateFlow(0)
    val categoryButtonColor = _categoryButtonColor.asStateFlow()

    private val _randomButtonColor = MutableStateFlow(0)
    val randomButtonColor = _randomButtonColor.asStateFlow()

    private val _searchButtonColor = MutableStateFlow(0)
    val searchButtonColor = _searchButtonColor.asStateFlow()

    private val _settingsButtonColor = MutableStateFlow(0)
    val settingsButtonColor = _settingsButtonColor.asStateFlow()

    private val _uploadButtonColor = MutableStateFlow(0)
    val uploadButtonColor = _uploadButtonColor.asStateFlow()

    init {
        viewModelScope.launch {
            navigationStateRepository.navArea
                .onEach { updateNavColors(it) }
                .launchIn(this)
        }
    }

    fun setTextColors(activeColor: Int, inactiveColor: Int) {
        _activeColor = activeColor
        _inActiveColor = inactiveColor
    }

    fun requestNavigateToArea(area: NavigationArea) {
        navigationStateRepository.requestNavigateToArea(area)
    }

    private fun updateNavColors(navigationArea: NavigationArea) {
        _aboutButtonColor.value = getColor(navigationArea == NavigationArea.About)
        _categoryButtonColor.value = getColor(navigationArea == NavigationArea.Category)
        _randomButtonColor.value = getColor(navigationArea == NavigationArea.Random)
        _searchButtonColor.value = getColor(navigationArea == NavigationArea.Search)
        _settingsButtonColor.value = getColor(navigationArea == NavigationArea.Settings)
        _uploadButtonColor.value = getColor(navigationArea == NavigationArea.Upload)
    }

    private fun getColor(isInArea: Boolean): Int {
        return if(isInArea) {
            _activeColor
        } else {
            _inActiveColor
        }
    }
}
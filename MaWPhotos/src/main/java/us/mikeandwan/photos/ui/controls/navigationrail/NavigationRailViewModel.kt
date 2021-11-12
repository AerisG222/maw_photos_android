package us.mikeandwan.photos.ui.controls.navigationrail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import us.mikeandwan.photos.R
import us.mikeandwan.photos.domain.models.NavigationArea
import us.mikeandwan.photos.domain.NavigationStateRepository
import javax.inject.Inject

@HiltViewModel
class NavigationRailViewModel @Inject constructor(
    private val navigationStateRepository: NavigationStateRepository
): ViewModel() {
    private val _aboutButtonColor = MutableStateFlow(R.color.material_on_surface_stroke)
    val aboutButtonColor = _aboutButtonColor.asStateFlow()

    private val _categoryButtonColor = MutableStateFlow(R.color.material_on_surface_stroke)
    val categoryButtonColor = _categoryButtonColor.asStateFlow()

    private val _randomButtonColor = MutableStateFlow(R.color.material_on_surface_stroke)
    val randomButtonColor = _randomButtonColor.asStateFlow()

    private val _settingsButtonColor = MutableStateFlow(R.color.material_on_surface_stroke)
    val settingsButtonColor = _settingsButtonColor.asStateFlow()

    private val _uploadButtonColor = MutableStateFlow(R.color.material_on_surface_stroke)
    val uploadButtonColor = _uploadButtonColor.asStateFlow()

    init {
        viewModelScope.launch {
            navigationStateRepository.navArea.collect { updateNavColors(it) }
        }
    }

    fun requestNavigateToArea(area: NavigationArea) {
        navigationStateRepository.requestNavigateToArea(area)
    }

    private fun updateNavColors(navigationArea: NavigationArea) {
        _aboutButtonColor.value = getColor(navigationArea == NavigationArea.About)
        _categoryButtonColor.value = getColor(navigationArea == NavigationArea.Category)
        _randomButtonColor.value = getColor(navigationArea == NavigationArea.Random)
        _settingsButtonColor.value = getColor(navigationArea == NavigationArea.Settings)
        _uploadButtonColor.value = getColor(navigationArea == NavigationArea.Upload)
    }

    private fun getColor(isInArea: Boolean): Int {
        return if(isInArea) {
            R.color.pink_700
        } else {
            R.color.material_on_surface_stroke
        }
    }
}
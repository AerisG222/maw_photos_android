package us.mikeandwan.photos.uinew.ui.navigationrail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import us.mikeandwan.photos.R
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

    init {
        viewModelScope.launch {
            navigationStateRepository.activeDestinationId.collect { updateNavColors(it) }
        }
    }

    fun requestNavigation(id: Int) {
        navigationStateRepository.requestNavigation(id)
    }

    private fun updateNavColors(id: Int) {
        _aboutButtonColor.value = getColor(id, R.id.navigation_about)
        _categoryButtonColor.value = getColor(id, R.id.navigation_categories)
        _randomButtonColor.value = getColor(id, R.id.navigation_random)
        _settingsButtonColor.value = getColor(id, R.id.navigation_settings)
    }

    private fun getColor(id: Int, otherId: Int): Int {
        return if(id == otherId) {
            R.color.pink_700
        } else {
            R.color.material_on_surface_stroke
        }
    }
}
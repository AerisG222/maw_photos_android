package us.mikeandwan.photos.ui.controls.navigationrail

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import us.mikeandwan.photos.domain.NavigationStateRepository
import us.mikeandwan.photos.domain.models.NavigationArea
import javax.inject.Inject

@HiltViewModel
class NavigationRailViewModel @Inject constructor(
    private val navigationStateRepository: NavigationStateRepository
): ViewModel() {
    val navArea = navigationStateRepository.navArea;

    fun requestNavigateToArea(area: NavigationArea) {
        navigationStateRepository.requestNavigateToArea(area)
    }
}
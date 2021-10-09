package us.mikeandwan.photos.uinew.ui.navigationRail

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import us.mikeandwan.photos.domain.NavigationStateRepository
import javax.inject.Inject

@HiltViewModel
class NavigationRailViewModel @Inject constructor(
    private val navigationStateRepository: NavigationStateRepository
): ViewModel() {
    fun requestClose() {
        navigationStateRepository.requestNavDrawerClose()
    }
}
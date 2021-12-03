package us.mikeandwan.photos.ui.controls.toolbar

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import us.mikeandwan.photos.domain.NavigationStateRepository
import javax.inject.Inject

@HiltViewModel
class ToolbarViewModel @Inject constructor(
    private val navigationStateRepository: NavigationStateRepository
): ViewModel() {
    val enableDrawer = navigationStateRepository.enableDrawer
    val toolbarTitle = navigationStateRepository.toolbarTitle

    fun onAppIconClicked() {
        navigationStateRepository.requestNavDrawerOpen()
    }

    fun onBackClicked() {
        navigationStateRepository.requestNavigateBack()
    }
}
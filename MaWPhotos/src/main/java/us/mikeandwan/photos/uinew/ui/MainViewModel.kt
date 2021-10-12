package us.mikeandwan.photos.uinew.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import us.mikeandwan.photos.R
import us.mikeandwan.photos.authorization.AuthService
import us.mikeandwan.photos.domain.NavigationStateRepository
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authService: AuthService,
    private val navigationStateRepository: NavigationStateRepository
) : ViewModel() {
    val isAuthenticated: StateFlow<Boolean>
        get() {
            return authService.isAuthorized
        }

    val enableDrawer = navigationStateRepository.enableDrawer
    val shouldCloseDrawer = navigationStateRepository.closeNavDrawerSignal
    val toolbarTitle = navigationStateRepository.toolbarTitle
    val navigationRequests = navigationStateRepository.requestedNavigation

    fun drawerClosed() {
        navigationStateRepository.closeNavDrawerCompleted()
    }

    fun destinationChanged(destinationId: Int) {
        viewModelScope.launch {
            navigationStateRepository.onDestinationChanged(destinationId)
        }
    }

    fun navigationRequestCompleted() {
        navigationStateRepository.requestNavigationCompleted()
    }
}
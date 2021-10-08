package us.mikeandwan.photos.uinew.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
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

    val shouldCloseDrawer = navigationStateRepository.closeSignal

    fun drawerClosed() {
        navigationStateRepository.closeCompleted()
    }
}
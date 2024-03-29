package us.mikeandwan.photos.ui.login

import android.content.Intent
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import us.mikeandwan.photos.authorization.AuthService
import us.mikeandwan.photos.domain.NavigationStateRepository
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    val authService: AuthService,
    val navigationStateRepository: NavigationStateRepository
): ViewModel() {
    private var _doAuth = MutableStateFlow(false)
    val doAuth = _doAuth.asStateFlow()

    val authStatus = authService.authStatus

    fun initiateAuthentication() {
        authService.beginAuthentication()
        _doAuth.value = true
    }

    fun initiateAuthenticationHandled() {
        _doAuth.value = false
    }

    fun completeAuthorization(intent: Intent) {
        authService.completeAuthorization(intent)
    }

    init {
        navigationStateRepository.setIsOnLoginScreen()
    }
}
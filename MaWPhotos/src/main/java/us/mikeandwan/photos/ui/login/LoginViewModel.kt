package us.mikeandwan.photos.ui.login

import android.content.Intent
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import us.mikeandwan.photos.authorization.AuthService
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(val authService: AuthService): ViewModel() {
    private var _doAuth = MutableStateFlow(false)
    val doAuth = _doAuth.asStateFlow()

    val isAuthorized = authService.isAuthorized

    fun initiateAuthentication() {
        _doAuth.value = true
    }

    fun initiateAuthenticationHandled() {
        _doAuth.value = false
    }

    suspend fun completeAuthorization(intent: Intent) {
        authService.completeAuthorization(intent)
    }
}
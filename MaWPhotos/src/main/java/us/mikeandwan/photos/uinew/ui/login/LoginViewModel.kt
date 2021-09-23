package us.mikeandwan.photos.uinew.ui.login

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import us.mikeandwan.photos.services.AuthService
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(val authService: AuthService): ViewModel() {
    private var _doAuth = MutableStateFlow(false)
    val doAuth: StateFlow<Boolean> = _doAuth

    fun initiateAuthentication() {
        _doAuth.value = true
    }

    fun initiateAuthenticationHandled() {
        _doAuth.value = false
    }
}
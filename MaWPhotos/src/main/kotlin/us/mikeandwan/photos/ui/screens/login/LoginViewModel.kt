package us.mikeandwan.photos.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import us.mikeandwan.photos.authorization.AuthService
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    val authService: AuthService
): ViewModel() {
    val authStatus = authService.authStatus

    fun initiateAuthentication() {
        viewModelScope.launch {
            authService.beginAuthentication()
        }
    }
}
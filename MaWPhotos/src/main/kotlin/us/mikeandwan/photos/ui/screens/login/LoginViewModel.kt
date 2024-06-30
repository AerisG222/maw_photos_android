package us.mikeandwan.photos.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import us.mikeandwan.photos.authorization.AuthService
import us.mikeandwan.photos.authorization.AuthStatus
import javax.inject.Inject

sealed class LoginState {
    data object Unknown : LoginState()
    data object Authorized : LoginState()
    data class NotAuthorized(
        val initiateAuthentication: () -> Unit
    ) : LoginState()
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authService: AuthService
): ViewModel() {
    val state = authService.authStatus
        .map {
            when(it) {
                is AuthStatus.Authorized -> LoginState.Authorized
                is AuthStatus.RequiresAuthorization -> LoginState.NotAuthorized(::initiateAuthentication)

                // when in process, return the notauthorized state, as this will catch the case where the user
                // starts authentication but does not complete that action (closing browser tab / going back / etc)
                is AuthStatus.LoginInProcess -> LoginState.NotAuthorized(::initiateAuthentication)
            }
        }
        .stateIn(viewModelScope, WhileSubscribed(5000), LoginState.Unknown)

    private fun initiateAuthentication() {
        viewModelScope.launch {
            authService.beginAuthentication()
        }
    }
}

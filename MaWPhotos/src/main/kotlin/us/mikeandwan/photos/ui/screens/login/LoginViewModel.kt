package us.mikeandwan.photos.ui.screens.login

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.TokenResponse
import timber.log.Timber
import us.mikeandwan.photos.authorization.ApplicationException
import us.mikeandwan.photos.authorization.AuthService
import us.mikeandwan.photos.authorization.AuthStatus
import us.mikeandwan.photos.domain.AuthorizationRepository
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
    private val authService: AuthService,
    private val authorizationRepository: AuthorizationRepository
): ViewModel() {
    private val _notifyStartLogin = MutableStateFlow<Intent?>(null)
    val notifyStartLogin = _notifyStartLogin.asStateFlow()

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

    fun handleAuthorizeCallback(intent: Intent) {
        val authorizationResponse = authService.completeAuthorization(
            AuthorizationResponse.fromIntent(intent),
            AuthorizationException.fromIntent(intent)
        )

        var tokenResponse: TokenResponse?

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    tokenResponse = authService.redeemCodeForTokens(authorizationResponse)

                    val curr = authorizationRepository.authState.value
                    curr.update(tokenResponse!!, null)
                    authorizationRepository.save(curr)
                } catch (ex: ApplicationException) {
                    Timber.e("Could not handle the authorization response", ex)
                }
            }
        }
    }

    private fun initiateAuthentication() {
        var cfg = authorizationRepository.authState.value.authorizationServiceConfiguration

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    if(cfg == null) {
                        cfg = authService.loadConfig()
                    }

                    withContext(Dispatchers.Main) {
                        _notifyStartLogin.value = authService.getAuthorizationRedirectIntent(cfg!!)
                    }
                } catch (ex: ApplicationException) {
                    withContext(Dispatchers.Main) {
                        Timber.e("could not initiate authorization", ex)
                    }
                }
            }
        }
    }
}

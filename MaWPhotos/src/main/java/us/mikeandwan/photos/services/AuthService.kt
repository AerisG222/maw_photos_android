package us.mikeandwan.photos.services

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import net.openid.appauth.*
import timber.log.Timber
import us.mikeandwan.photos.Constants
import us.mikeandwan.photos.R
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class AuthService(
    application: Application,
    val authorizationService: AuthorizationService,
    private val authStateManager: AuthStateManager
) {
    val authClientId: String = application.resources.getString(R.string.auth_client_id)
    val authSchemeRedirect: String = application.resources.getString(R.string.auth_scheme_redirect_uri)
    val authSchemeRedirectUri: Uri = Uri.parse(authSchemeRedirect)

    private var _authConfig: AuthorizationServiceConfiguration? = null;
    val authConfig: AuthorizationServiceConfiguration?
        get() { return _authConfig }

    private val _isAuthorized = MutableStateFlow(authStateManager.current.isAuthorized)
    val isAuthorized: StateFlow<Boolean> = _isAuthorized

    fun updateAuthorizationState(isAuthorized: Boolean) {
        _isAuthorized.value = isAuthorized
    }

    suspend fun clearAuthState() {
        _authConfig = loadConfig()

        authStateManager.replace(AuthState(_authConfig!!))
    }

    suspend fun loadConfig(): AuthorizationServiceConfiguration {
        return suspendCoroutine { cont ->
            AuthorizationServiceConfiguration.fetchFromIssuer(
                Uri.parse(Constants.AUTH_BASE_URL)
            ) { serviceConfiguration: AuthorizationServiceConfiguration?, ex: AuthorizationException? ->
                if (ex != null) {
                    Timber.e("failed to fetch openidc configuration")
                    cont.resumeWithException(ex)
                }

                if (serviceConfiguration != null) {
                    cont.resumeWith(Result.success(serviceConfiguration))
                }
            }
        }
    }

    suspend fun completeAuthorization(intent: Intent) {
        val response = AuthorizationResponse.fromIntent(intent)
        val ex = AuthorizationException.fromIntent(intent)

        if (response != null || ex != null) {
            authStateManager.updateAfterAuthorization(response, ex)
        }

        when {
            response?.authorizationCode != null -> {
                exchangeAuthorizationCode(response)
            }
            ex != null -> {
                Timber.e("Authorization failed: %s", ex.message)
            }
            else -> {
                Timber.e("No authorization state retained - reauthorization required")
            }
        }
    }

    private suspend fun exchangeAuthorizationCode(authorizationResponse: AuthorizationResponse) {
        Timber.d("Exchanging authorization code")

        performTokenRequest(
            authorizationResponse.createTokenExchangeRequest()
        ) { tokenResponse: TokenResponse?, authException: AuthorizationException? ->
            handleCodeExchangeResponse(
                tokenResponse,
                authException
            )
        }
    }

    private suspend fun performTokenRequest(request: TokenRequest, callback: AuthorizationService.TokenResponseCallback) {
        val clientAuthentication: ClientAuthentication = try {
            Timber.d("Attempting token request")
            authStateManager.current.clientAuthentication
        } catch (ex: ClientAuthentication.UnsupportedAuthenticationMethod) {
            Timber.d(
                "Token request cannot be made, client authentication for the token endpoint could not be constructed (%s)",
                ex.message
            )

            return
        }

        authorizationService.performTokenRequest(
            request,
            clientAuthentication,
            callback
        )
    }

    private fun handleCodeExchangeResponse(
        tokenResponse: TokenResponse?,
        authException: AuthorizationException?
    ) {
        authStateManager.updateAfterTokenResponse(tokenResponse, authException)

        updateAuthorizationState(authStateManager.current.isAuthorized)

        if (!authStateManager.current.isAuthorized) {
            val message =
                "Authorization Code exchange failed" + if (authException != null) authException.error else ""
            Timber.e("NOT AUTHORIZED: %s", message)
        } else {
            Timber.d("AUTHORIZED")
            Timber.d("auth token: %s", authStateManager.current.accessToken)
            Timber.d("refresh token: %s", authStateManager.current.refreshToken)
            Timber.d("id token: %s", authStateManager.current.idToken)
        }
    }
}

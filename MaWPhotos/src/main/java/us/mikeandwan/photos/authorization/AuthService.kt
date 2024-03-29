package us.mikeandwan.photos.authorization

import android.app.Application
import android.content.Intent
import android.net.Uri
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import net.openid.appauth.*
import timber.log.Timber
import us.mikeandwan.photos.Constants
import us.mikeandwan.photos.R
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class AuthService(
    application: Application,
    private val authorizationService: AuthorizationService,
    private val authStateManager: AuthStateManager
) {
    val authClientId: String = application.resources.getString(R.string.auth_client_id)
    private val authSchemeRedirect: String = application.resources.getString(R.string.auth_scheme_redirect_uri)
    val authSchemeRedirectUri: Uri = Uri.parse(authSchemeRedirect)

    private var _authConfig: AuthorizationServiceConfiguration? = null
    val authConfig: AuthorizationServiceConfiguration?
        get() { return _authConfig }

    private val _authStatus = MutableStateFlow(if(authStateManager.current.isAuthorized) AuthStatus.Authorized else AuthStatus.RequiresAuthorization)
    val authStatus = _authStatus.asStateFlow()

    fun beginAuthentication() {
        _authStatus.value = AuthStatus.LoginInProcess
    }

    private fun updateAuthorizationState(isAuthorized: Boolean) {
        _authStatus.value = if(isAuthorized) AuthStatus.Authorized else AuthStatus.RequiresAuthorization
    }

    fun logout() {
        _authStatus.value = AuthStatus.RequiresAuthorization
    }

    suspend fun clearAuthState() {
        _authConfig = loadConfig()

        authStateManager.replace(AuthState(_authConfig!!))
    }

    private suspend fun loadConfig(): AuthorizationServiceConfiguration {
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

    fun completeAuthorization(intent: Intent) {
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

    private fun exchangeAuthorizationCode(authorizationResponse: AuthorizationResponse) {
        Timber.d("Exchanging authorization code")

        performTokenRequest(
            authorizationResponse.createTokenExchangeRequest()
        ) { tokenResponse, authException ->
            handleCodeExchangeResponse(tokenResponse, authException)
        }
    }

    private fun performTokenRequest(request: TokenRequest, callback: AuthorizationService.TokenResponseCallback) {
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

        authorizationService.performTokenRequest(request, clientAuthentication, callback)
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

package us.mikeandwan.photos.services

import android.app.Application
import android.net.Uri
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
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

    val isAuthorized: Boolean
        get() {
            return authStateManager.current.isAuthorized
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
}

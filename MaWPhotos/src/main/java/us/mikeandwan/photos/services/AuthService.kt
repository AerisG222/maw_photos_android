package us.mikeandwan.photos.services

import android.app.Application
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import net.openid.appauth.AuthorizationService
import us.mikeandwan.photos.R

class AuthService(
    application: Application,
    authorizationService: AuthorizationService,
    private val authStateManager: AuthStateManager
) {
    private val _authClientId: String = application.resources.getString(R.string.auth_client_id)
    private val _authSchemeRedirect: String = application.resources.getString(R.string.auth_scheme_redirect_uri)
    private val _authSchemeRedirectUri: Uri = Uri.parse(_authSchemeRedirect)

    val isAuthorized: Boolean
        get() {
            return authStateManager.current.isAuthorized
        }
}

package us.mikeandwan.photos.ui.loginCallback

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.Observable
import net.openid.appauth.*
import net.openid.appauth.AuthorizationService.TokenResponseCallback
import net.openid.appauth.ClientAuthentication.UnsupportedAuthenticationMethod
import timber.log.Timber
import us.mikeandwan.photos.R
import us.mikeandwan.photos.services.AuthService
import us.mikeandwan.photos.services.AuthStateManager
import us.mikeandwan.photos.ui.BaseActivity
import us.mikeandwan.photos.ui.initialLoad.InitialLoadActivity
import us.mikeandwan.photos.ui.login.LoginActivity
import javax.inject.Inject

@AndroidEntryPoint
class LoginCallbackActivity : BaseActivity() {
    @Inject lateinit var _authStateManager: AuthStateManager
    @Inject lateinit var _authService: AuthorizationService
    @Inject lateinit var _authSvc: AuthService

    // https://github.com/openid/AppAuth-Android/blob/master/app/java/net/openid/appauthdemo/TokenActivity.java
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_callback)
    }

    public override fun onStart() {
        super.onStart()

        if (_authStateManager.current.isAuthorized) {
            goToInitialLoad()
        }

        val response = AuthorizationResponse.fromIntent(intent)
        val ex = AuthorizationException.fromIntent(intent)

        if (response != null || ex != null) {
            _authStateManager.updateAfterAuthorization(response, ex)
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

    @MainThread
    private fun exchangeAuthorizationCode(authorizationResponse: AuthorizationResponse) {
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

    @MainThread
    private fun performTokenRequest(request: TokenRequest, callback: TokenResponseCallback) {
        val clientAuthentication: ClientAuthentication = try {
            Timber.d("Attempting token request")
            _authStateManager.current.clientAuthentication
        } catch (ex: UnsupportedAuthenticationMethod) {
            Timber.d(
                "Token request cannot be made, client authentication for the token endpoint could not be constructed (%s)",
                ex.message
            )

            return
        }

        _authService.performTokenRequest(
            request,
            clientAuthentication,
            callback
        )
    }

    @WorkerThread
    private fun handleCodeExchangeResponse(
        tokenResponse: TokenResponse?,
        authException: AuthorizationException?
    ) {
        _authStateManager.updateAfterTokenResponse(tokenResponse, authException)

        _authSvc.updateAuthorizationState(_authStateManager.current.isAuthorized)

        if (!_authStateManager.current.isAuthorized) {
            val message =
                "Authorization Code exchange failed" + if (authException != null) authException.error else ""
            Timber.e("NOT AUTHORIZED: %s", message)
        } else {
            Timber.d("AUTHORIZED")
            Timber.d("auth token: %s", _authStateManager.current.accessToken)
            Timber.d("refresh token: %s", _authStateManager.current.refreshToken)
            Timber.d("id token: %s", _authStateManager.current.idToken)
            goToInitialLoad()
        }
    }

    private fun goToInitialLoad() {
        val intent = Intent(this, InitialLoadActivity::class.java)

        startActivity(intent)
        finish()
    }

    fun retryLogin(view: View) {
        val intent = Intent(this, LoginActivity::class.java)

        startActivity(intent)
        finish()
    }
}
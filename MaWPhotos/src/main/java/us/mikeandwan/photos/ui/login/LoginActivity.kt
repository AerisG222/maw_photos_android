package us.mikeandwan.photos.ui.login

import dagger.hilt.android.AndroidEntryPoint
import us.mikeandwan.photos.ui.BaseActivity
import io.reactivex.disposables.CompositeDisposable
import us.mikeandwan.photos.R
import javax.inject.Inject
import us.mikeandwan.photos.services.AuthStateManager
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.AuthorizationService
import android.os.Bundle
import android.content.Intent
import us.mikeandwan.photos.ui.mode.ModeSelectionActivity
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.ResponseTypeValues
import android.app.PendingIntent
import android.net.Uri
import android.view.View
import io.reactivex.Observable
import us.mikeandwan.photos.ui.loginCallback.LoginCallbackActivity
import timber.log.Timber

@AndroidEntryPoint
class LoginActivity : BaseActivity() {
    private val _disposables = CompositeDisposable()

    lateinit var _authClientId: String
    lateinit var _authSchemeRedirect: String

    @Inject lateinit var _authStateManager: AuthStateManager
    @Inject lateinit var _config: Observable<AuthorizationServiceConfiguration>

    var _authService: AuthorizationService? = null

    private var _authSchemeRedirectUri: Uri? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        _authClientId = resources.getString(R.string.auth_client_id) as String
        _authSchemeRedirect = resources.getString(R.string.auth_scheme_redirect_uri)
        _authSchemeRedirectUri = Uri.parse(_authSchemeRedirect)

        // https://github.com/openid/AppAuth-Android/issues/333
        _authService = AuthorizationService(this)
        authorize()
    }

    public override fun onDestroy() {
        _disposables.clear()
        _authService!!.dispose()
        super.onDestroy()
    }

    private fun goToModeSelection() {
        val intent = Intent(this, ModeSelectionActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun authorize() {
        if (isAuthorized) {
            // we go to mode selection here, because if a user has previously gained access,
            // if they are off/slow network, then they get stuck for a bit on the blank loading
            // screen which might not be needed - so take them straight in
            goToModeSelection()
            return
        }
        _disposables.add(_config!!.subscribe({ config: AuthorizationServiceConfiguration? ->
            _authStateManager!!.replace(AuthState(config!!))
            val authRequestBuilder = AuthorizationRequest.Builder(
                config,
                _authClientId!!,  // the client ID, typically pre-registered and static
                ResponseTypeValues.CODE,  // the response_type value: we want a code
                _authSchemeRedirectUri!!
            ) // the redirect URI to which the auth response is sent
            val authRequest = authRequestBuilder
                .setScopes("openid offline_access profile email role maw_api")
                .build()
            _authService!!.performAuthorizationRequest(
                authRequest,
                PendingIntent.getActivity(
                    this,
                    0,
                    Intent(this, LoginCallbackActivity::class.java),
                    0
                ),
                PendingIntent.getActivity(this, 0, Intent(this, LoginActivity::class.java), 0)
            )
        }) { ex: Throwable ->
            Timber.e("There was an error getting OIDC configuration: %s", ex.message)
            handleApiException(ex)
        })
    }

    private val isAuthorized: Boolean
        private get() {
            val authState = _authStateManager!!.current
            return authState.isAuthorized
        }

    private fun retryLogin(view: View) {
        recreate()
    }
}
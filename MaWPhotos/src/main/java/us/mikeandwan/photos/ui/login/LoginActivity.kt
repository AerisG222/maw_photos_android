package us.mikeandwan.photos.ui.login

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.ResponseTypeValues
import us.mikeandwan.photos.authorization.AuthStatus
import us.mikeandwan.photos.ui.main.MainActivity
import us.mikeandwan.photos.utils.PendingIntentFlagHelper

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var authService: AuthorizationService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authService = AuthorizationService(this)

        setContent {
            LoginScreen(viewModel)
        }

        // try to handle the login result in case we are returning from the auth site
        viewModel.completeAuthorization(intent)

        initStateObservers()
    }

    override fun isDestroyed(): Boolean {
        authService.dispose()

        return super.isDestroyed()
    }

    private fun initStateObservers() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.doAuth
                    .filter { it }
                    .onEach {
                        viewModel.initiateAuthenticationHandled()
                        initiateAuthentication()
                    }
                    .launchIn(this)

                viewModel.authStatus
                    .filter { it !is AuthStatus.LoginInProcess }
                    .onEach {
                        when(it) {
                            is AuthStatus.LoginInProcess -> {}
                            is AuthStatus.Authorized -> goToNextScreen()
                            is AuthStatus.RequiresAuthorization -> viewModel.initiateAuthentication()
                        }
                    }
                    .launchIn(this)
            }
        }
    }

    private fun goToNextScreen() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    // ideally this would be in the view model / auth service, but we need access to the activity
    // context to bring up the browser to do the authentication, so need to execute this here.
    // we also need to use a new AuthorizationService that does not come from di so it is bound
    // to the activity. see: https://github.com/openid/AppAuth-Android/issues/333
    private fun initiateAuthentication() {
        val activity = this

        lifecycleScope.launch {
            viewModel.authService.clearAuthState()

            val request = buildAuthorizationRequest()

            authService.performAuthorizationRequest(
                request,
                PendingIntent.getActivity(
                    activity,
                    0,
                    Intent(activity, LoginActivity::class.java),
                    PendingIntentFlagHelper.getMutableFlag(0)
                ),
                PendingIntent.getActivity(
                    activity,
                    0,
                    Intent(activity, LoginActivity::class.java),
                    PendingIntentFlagHelper.getMutableFlag(0)
                )
            )
        }
    }

    private fun buildAuthorizationRequest(): AuthorizationRequest {
        return AuthorizationRequest.Builder(
                viewModel.authService.authConfig!!,
                viewModel.authService.authClientId,
                ResponseTypeValues.CODE,
                viewModel.authService.authSchemeRedirectUri
            )
            .setScope("openid offline_access profile email role maw_api")
            .build()
    }
}

package us.mikeandwan.photos.uinew.ui.login

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import net.openid.appauth.*
import us.mikeandwan.photos.R
import us.mikeandwan.photos.databinding.ActivityLoginBinding
import us.mikeandwan.photos.utils.PendingIntentFlagHelper
import us.mikeandwan.photos.uinew.ui.main.MainActivity

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var authService: AuthorizationService
    private val viewModel by viewModels<LoginViewModel>()

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (viewModel.authService.isAuthorized.value) {
            goToNextScreen()
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        authService = AuthorizationService(this)

        lifecycleScope.launch {
            // try to handle the login result in case we are returning from the auth site
            viewModel.completeAuthorization(intent)

            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.doAuth.collect { doAuth ->
                    if (doAuth) {
                        initiateAuthentication()
                        viewModel.initiateAuthenticationHandled()
                    }
                }

                viewModel.isAuthorized.collect { isAuthorized ->
                    if(isAuthorized) {
                        goToNextScreen()
                    }
                }
            }
        }

        if (!viewModel.authService.isAuthorized.value) {
            initiateAuthentication()
        }
    }

    override fun isDestroyed(): Boolean {
        authService.dispose()
        return super.isDestroyed()
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

            //Intent(activity, LoginCallbackActivity::class.java),
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
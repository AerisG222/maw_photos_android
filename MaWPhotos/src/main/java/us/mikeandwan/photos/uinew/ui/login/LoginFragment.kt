package us.mikeandwan.photos.uinew.ui.login

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.ResponseTypeValues
import us.mikeandwan.photos.R
import us.mikeandwan.photos.services.PendingIntentFlagHelper
import us.mikeandwan.photos.ui.login.LoginActivity
import us.mikeandwan.photos.ui.loginCallback.LoginCallbackActivity
import us.mikeandwan.photos.databinding.FragmentLoginBinding

@AndroidEntryPoint
class LoginFragment : Fragment() {
    companion object {
        fun newInstance() = LoginFragment()
    }

    private lateinit var binding: FragmentLoginBinding
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.doAuth.collect { doAuth ->
                    if (doAuth) {
                        initiateAuthentication()
                        viewModel.initiateAuthenticationHandled()
                    }
                }
            }
        }

        if (viewModel.authService.isAuthorized.value) {
            goToNextScreen()
        } else {
            initiateAuthentication()
        }
    }

    private fun goToNextScreen() {
        // val action = LoginFragmentDirections.actionNavigationLoginToNavigationCategories()
        // findNavController().navigate(action)
    }

    // ideally this would be in the view model / auth service, but we need access to the activity
    // context to bring up the browser to do the authentication, so need to execute this here.
    // we also need to use a new AuthorizationService that does not come from di so it is bound
    // to the activity. see: https://github.com/openid/AppAuth-Android/issues/333
    private fun initiateAuthentication() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.authService.clearAuthState()

            val request = buildAuthorizationRequest()
            val authorizationService = AuthorizationService(requireActivity())

            authorizationService.performAuthorizationRequest(
                request,
                PendingIntent.getActivity(
                    activity,
                    0,
                    Intent(activity, LoginCallbackActivity::class.java),
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
                viewModel.authService.authClientId,  // the client ID, typically pre-registered and static
                ResponseTypeValues.CODE,  // the response_type value: we want a code
                viewModel.authService.authSchemeRedirectUri
            )
            .setScope("openid offline_access profile email role maw_api")
            .build()
    }
}
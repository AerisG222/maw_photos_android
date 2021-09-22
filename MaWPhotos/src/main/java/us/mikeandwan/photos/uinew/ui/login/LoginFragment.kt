package us.mikeandwan.photos.uinew.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import us.mikeandwan.photos.R

@AndroidEntryPoint
class LoginFragment : Fragment() {
    companion object {
        fun newInstance() = LoginFragment()
    }

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if(viewModel.authService.isAuthorized) {
            goToNextScreen()
        } else {
            viewModel.initiateAuthentication()
        }
    }

    fun goToNextScreen() {
        val action = LoginFragmentDirections.actionLoginFragmentToNavigationYears()
        findNavController().navigate(action)
    }
}
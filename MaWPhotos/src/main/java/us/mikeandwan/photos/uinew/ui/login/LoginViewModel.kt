package us.mikeandwan.photos.uinew.ui.login

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import us.mikeandwan.photos.services.AuthService
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(val authService: AuthService): ViewModel() {
    fun initiateAuthentication() {
        TODO("Not yet implemented")
    }
}
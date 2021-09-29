package us.mikeandwan.photos.uinew.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import us.mikeandwan.photos.authorization.AuthService
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val authService: AuthService) : ViewModel() {
    val isAuthenticated: StateFlow<Boolean>
        get() {
            return authService.isAuthorized
        }
}
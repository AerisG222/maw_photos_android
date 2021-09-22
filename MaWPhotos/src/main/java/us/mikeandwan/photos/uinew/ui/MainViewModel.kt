package us.mikeandwan.photos.uinew.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import us.mikeandwan.photos.services.AuthService
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(authService: AuthService) : ViewModel() {
    private val _isAuthenticated = MutableStateFlow(authService.isAuthorized)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated
}
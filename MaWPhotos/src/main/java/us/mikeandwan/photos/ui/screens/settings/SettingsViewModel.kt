package us.mikeandwan.photos.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import us.mikeandwan.photos.authorization.AuthService
import us.mikeandwan.photos.domain.NavigationStateRepository
import us.mikeandwan.photos.domain.models.NavigationArea
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor (
    private val authService: AuthService,
    private val navigationStateRepository: NavigationStateRepository
): ViewModel() {
    fun logout() {
        viewModelScope.launch {
            authService.logout()
            navigationStateRepository.requestNavigateToArea(NavigationArea.Login)
        }
    }
}
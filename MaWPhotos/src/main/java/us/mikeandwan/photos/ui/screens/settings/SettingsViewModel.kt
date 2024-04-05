package us.mikeandwan.photos.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import us.mikeandwan.photos.authorization.AuthService
import us.mikeandwan.photos.domain.CategoryPreferenceRepository
import us.mikeandwan.photos.domain.ErrorRepository
import us.mikeandwan.photos.domain.NavigationStateRepository
import us.mikeandwan.photos.domain.NotificationPreferenceRepository
import us.mikeandwan.photos.domain.PhotoPreferenceRepository
import us.mikeandwan.photos.domain.RandomPreferenceRepository
import us.mikeandwan.photos.domain.SearchPreferenceRepository
import us.mikeandwan.photos.domain.models.NavigationArea
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor (
    private val authService: AuthService,
    private val navigationStateRepository: NavigationStateRepository,
    val categoryPreferenceRepository: CategoryPreferenceRepository,
    val notificationPreferenceRepository: NotificationPreferenceRepository,
    val photoPreferenceRepository: PhotoPreferenceRepository,
    val randomPreferenceRepository: RandomPreferenceRepository,
    val searchPreferenceRepository: SearchPreferenceRepository,
    val errorRepository: ErrorRepository
): ViewModel() {
    fun logout() {
        viewModelScope.launch {
            authService.logout()
            navigationStateRepository.requestNavigateToArea(NavigationArea.Login)
        }
    }
}
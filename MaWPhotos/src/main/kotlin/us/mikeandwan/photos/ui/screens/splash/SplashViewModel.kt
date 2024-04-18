package us.mikeandwan.photos.ui.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import us.mikeandwan.photos.authorization.AuthService
import us.mikeandwan.photos.domain.PhotoCategoryRepository
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    val authService: AuthService,
    val photoCategoryRepository: PhotoCategoryRepository
): ViewModel() {
    val mostRecentYear = photoCategoryRepository.getMostRecentYear()
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val authStatus = authService.authStatus
}
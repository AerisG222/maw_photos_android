package us.mikeandwan.photos.ui.main

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import us.mikeandwan.photos.authorization.AuthService
import us.mikeandwan.photos.domain.FileStorageRepository
import us.mikeandwan.photos.domain.NavigationStateRepository
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    authService: AuthService,
    private val navigationStateRepository: NavigationStateRepository,
    private val fileStorageRepository: FileStorageRepository
) : ViewModel() {
    val isAuthenticated = authService.isAuthorized
    val enableDrawer = navigationStateRepository.enableDrawer
    val shouldCloseDrawer = navigationStateRepository.closeNavDrawerSignal
    val shouldOpenDrawer = navigationStateRepository.openNavDrawerSignal
    val shouldNavigateBack = navigationStateRepository.navigateBackSignal
    val navigationRequests = navigationStateRepository.requestedNavigation

    fun drawerClosed() {
        navigationStateRepository.closeNavDrawerCompleted()
    }

    fun drawerOpened() {
        navigationStateRepository.openNavDrawerCompleted()
    }

    fun destinationChanged(destinationId: Int) {
        viewModelScope.launch {
            navigationStateRepository.onDestinationChanged(destinationId)
        }
    }

    fun navigationBackCompleted() {
        navigationStateRepository.navigateBackCompleted()
    }

    fun navigationRequestCompleted() {
        navigationStateRepository.requestNavigationCompleted()
    }

    fun requestNavDrawerClose() {
        navigationStateRepository.requestNavDrawerClose()
    }

    suspend fun clearFileCache() {
        fileStorageRepository.clearShareCache()
        fileStorageRepository.clearLegacyFiles()
    }

    suspend fun saveUploadFile(mediaUri: Uri): File? {
        return fileStorageRepository.saveFileToUpload(mediaUri)
    }

    init {
        viewModelScope.launch {
            fileStorageRepository.refreshPendingUploads()
        }
    }
}
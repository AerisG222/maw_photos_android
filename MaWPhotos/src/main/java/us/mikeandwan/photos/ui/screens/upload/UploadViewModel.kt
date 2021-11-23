package us.mikeandwan.photos.ui.screens.upload

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import us.mikeandwan.photos.domain.FileStorageRepository
import javax.inject.Inject

@HiltViewModel
class UploadViewModel @Inject constructor (
    fileStorageRepository: FileStorageRepository
): ViewModel() {
    val filesToUpload = fileStorageRepository.pendingUploads

    init {
        viewModelScope.launch {
            fileStorageRepository.refreshPendingUploads()
        }
    }
}
package us.mikeandwan.photos.ui.screens.upload

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import us.mikeandwan.photos.domain.FileStorageRepository
import javax.inject.Inject

@HiltViewModel
class UploadViewModel @Inject constructor (
    fileStorageRepository: FileStorageRepository
): ViewModel() {
    val filesToUpload = fileStorageRepository.pendingUploads
}
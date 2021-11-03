package us.mikeandwan.photos.ui.screens.upload

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import us.mikeandwan.photos.domain.FileStorageRepository
import java.io.File
import javax.inject.Inject

@HiltViewModel
class UploadViewModel @Inject constructor (
    fileStorageRepository: FileStorageRepository
): ViewModel() {
    val filesToUpload = fileStorageRepository
        .getPendingUploads()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList<File>())
}
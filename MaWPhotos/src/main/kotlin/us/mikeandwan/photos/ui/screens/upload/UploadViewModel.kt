package us.mikeandwan.photos.ui.screens.upload

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import us.mikeandwan.photos.domain.FileStorageRepository

data class UploadUiState(
    val filesToUpload: List<File> = emptyList(),
    val isLoading: Boolean = true,
)

@HiltViewModel
class UploadViewModel
    @Inject
    constructor(
        private val fileStorageRepository: FileStorageRepository,
    ) : ViewModel() {
        val uiState = fileStorageRepository.pendingUploads
            .map { files ->
                UploadUiState(
                    filesToUpload = files,
                    isLoading = false,
                )
            }.stateIn(viewModelScope, WhileSubscribed(5000), UploadUiState())

        init {
            viewModelScope.launch {
                fileStorageRepository.refreshPendingUploads()
            }
        }
    }

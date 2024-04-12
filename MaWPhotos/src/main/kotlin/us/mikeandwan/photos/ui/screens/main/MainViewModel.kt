package us.mikeandwan.photos.ui.screens.main

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import timber.log.Timber
import us.mikeandwan.photos.MawApplication
import us.mikeandwan.photos.authorization.AuthService
import us.mikeandwan.photos.authorization.AuthStatus
import us.mikeandwan.photos.domain.FileStorageRepository
import us.mikeandwan.photos.domain.PhotoCategoryRepository
import us.mikeandwan.photos.workers.UploadWorker
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val authService: AuthService,
    private val photoCategoryRepository: PhotoCategoryRepository,
    private val fileStorageRepository: FileStorageRepository,
): ViewModel() {
    fun handleAuthorizeCallback(intent: Intent) {
        authService.completeAuthorization(intent)
    }

    fun handleSendSingle(intent: Intent) {
        val mediaUri = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
        }

        if(mediaUri != null) {
            enqueueUpload(mediaUri)
        }
    }

    fun handleSendMultiple(intent: Intent) {
        val mediaUris = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM, Uri::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableArrayListExtra<Uri?>(Intent.EXTRA_STREAM)
        }

        if(mediaUris != null) {
            enqueueUpload(*mediaUris.toTypedArray())
        }
    }

    private fun enqueueUpload(vararg mediaUri: Uri) {
        viewModelScope.launch {
            mediaUri.forEach {
                val file = saveUploadFile(it)

                if (file != null) {
                    val constraints = Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.UNMETERED)
                        .build()

                    val data = workDataOf(
                        UploadWorker.KEY_FILENAME to file.path
                    )

                    val work = OneTimeWorkRequestBuilder<UploadWorker>()
                        .setBackoffCriteria(
                            BackoffPolicy.EXPONENTIAL,
                            1,
                            TimeUnit.MINUTES
                        )
                        .setConstraints(constraints)
                        .setInputData(data)
                        .build()

                    val workManager = WorkManager.getInstance(MawApplication.instance)

                    workManager.enqueueUniqueWork(
                        "upload ${file.path}",
                        ExistingWorkPolicy.REPLACE,
                        work
                    )
                }
            }
        }
    }

    suspend fun clearFileCache() {
        fileStorageRepository.clearLegacyDatabase()
        fileStorageRepository.clearShareCache()
        fileStorageRepository.clearLegacyFiles()
    }

    private suspend fun saveUploadFile(mediaUri: Uri): File? {
        return fileStorageRepository.saveFileToUpload(mediaUri)
    }

    init {
        viewModelScope.launch {
            fileStorageRepository.refreshPendingUploads()

            authService.authStatus
                .filter { it is AuthStatus.Authorized }
                .collect {
                    Timber.i("User authorized - fetching categories")
                    photoCategoryRepository.getYears().collect {}
                }
        }
    }
}
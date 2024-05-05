package us.mikeandwan.photos.ui.screens.main

import android.content.Intent
import android.net.Uri
import androidx.core.content.IntentCompat
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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import us.mikeandwan.photos.MawApplication
import us.mikeandwan.photos.authorization.AuthService
import us.mikeandwan.photos.authorization.AuthStatus
import us.mikeandwan.photos.domain.ErrorRepository
import us.mikeandwan.photos.domain.FileStorageRepository
import us.mikeandwan.photos.domain.PhotoCategoryRepository
import us.mikeandwan.photos.domain.RandomPhotoRepository
import us.mikeandwan.photos.domain.SearchRepository
import us.mikeandwan.photos.domain.models.ErrorMessage
import us.mikeandwan.photos.workers.UploadWorker
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val authService: AuthService,
    private val photoCategoryRepository: PhotoCategoryRepository,
    private val fileStorageRepository: FileStorageRepository,
    private val searchRepository: SearchRepository,
    private val randomPhotoRepository: RandomPhotoRepository,
    errorRepository: ErrorRepository
): ViewModel() {
    val mostRecentYear = photoCategoryRepository.getMostRecentYear()

    val years = photoCategoryRepository.getYears()

    val errorsToDisplay = errorRepository.error
        .filter { it is ErrorMessage.Display }
        .map { it as ErrorMessage.Display }

    val recentSearchTerms = searchRepository
        .getSearchHistory()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun clearSearchHistory() {
        viewModelScope.launch {
            searchRepository.clearHistory()
        }
    }

    fun fetchRandomPhotos(count: Int) {
        viewModelScope.launch {
            randomPhotoRepository
                .fetch(count)
                .collect { }
        }
    }

    fun clearRandomPhotos() {
        randomPhotoRepository.clear()
    }

    fun handleAuthorizeCallback(intent: Intent) {
        authService.completeAuthorization(intent)
    }

    fun handleSendSingle(intent: Intent) {
        val mediaUri = IntentCompat.getParcelableExtra(
            intent,
            Intent.EXTRA_STREAM,
            Uri::class.java
        )

        if(mediaUri != null) {
            enqueueUpload(mediaUri)
        }
    }

    fun handleSendMultiple(intent: Intent) {
        val mediaUris = IntentCompat.getParcelableArrayListExtra(
            intent,
            Intent.EXTRA_STREAM,
            Uri::class.java
        )

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

    private suspend fun clearFileCache() {
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

            photoCategoryRepository.getNewCategories()

            clearFileCache()
        }
    }
}

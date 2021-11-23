package us.mikeandwan.photos.workers

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import us.mikeandwan.photos.MawApplication
import us.mikeandwan.photos.R
import us.mikeandwan.photos.api.PhotoApiClient
import us.mikeandwan.photos.domain.FileStorageRepository
import us.mikeandwan.photos.domain.NotificationPreferenceRepository
import us.mikeandwan.photos.utils.PendingIntentFlagHelper
import java.io.File

@HiltWorker
class UploadWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val apiClient: PhotoApiClient,
    private val preferenceRepository: NotificationPreferenceRepository,
    private val notificationManager: NotificationManager,
    private val fileStorageRepository: FileStorageRepository
): CoroutineWorker(appContext, params) {
    companion object {
        const val KEY_FILENAME = "filename"
        const val KEY_FAILURE_REASON = "failure_reason"
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val file = inputData.getString(KEY_FILENAME)
        val fileToUpload = getValidatedFile(file)
            ?: return@withContext Result.failure(
                workDataOf(KEY_FAILURE_REASON to "invalid file: $file")
            )

        try {
            apiClient.uploadFile(fileToUpload)
            fileToUpload.delete()
            showNotification(true)
            fileStorageRepository.refreshPendingUploads()

            Result.success()
        } catch(error: Throwable) {
            showNotification(false)
            fileStorageRepository.refreshPendingUploads()

            Result.failure(
                workDataOf(KEY_FAILURE_REASON to error.message)
            )
        }
    }

    private fun getValidatedFile(filename: String?): File? {
        if (filename == null || filename.isBlank() || !File(filename).exists()) {
            return null
        }

        return File(filename)
    }

    private suspend fun showNotification(wasSuccessful: Boolean) {
        val i = Intent(Intent.ACTION_MAIN)
        val pendingIntentFlag = PendingIntentFlagHelper.getMutableFlag(PendingIntent.FLAG_UPDATE_CURRENT)
        val detailsIntent = PendingIntent.getActivity(applicationContext, 0, i, pendingIntentFlag)

        val title = if(wasSuccessful) "Media Uploaded!" else "Upload Failed"
        val msg = if(wasSuccessful) "File uploaded.  Go to files.mikeandwan.us to manage your files." else "File upload will be tried again later."

        val builder =
            NotificationCompat.Builder(applicationContext, MawApplication.NOTIFICATION_CHANNEL_ID_UPLOAD_FILES)
                .setSmallIcon(R.drawable.ic_status_notification)
                .setContentTitle(title)
                .setContentText(msg)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(detailsIntent)
                .setAutoCancel(true)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)

        if (preferenceRepository.getDoVibrate().first()) {
            builder.setVibrate(longArrayOf(300, 300))
        }

        val notification = builder.build()

        notificationManager.notify(0, notification)
    }
}
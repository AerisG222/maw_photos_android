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
import kotlinx.coroutines.flow.first
import us.mikeandwan.photos.MawApplication
import us.mikeandwan.photos.R
import us.mikeandwan.photos.api.PhotoApiClient
import us.mikeandwan.photos.domain.NotificationPreferenceRepository
import us.mikeandwan.photos.utils.PendingIntentFlagHelper
import java.io.File

@HiltWorker
class UploadWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val apiClient: PhotoApiClient,
    private val preferenceRepository: NotificationPreferenceRepository,
    private val notificationManager: NotificationManager
): CoroutineWorker(appContext, params) {
    companion object {
        const val KEY_FILENAME = "filename"
        const val KEY_FAILURE_REASON = "failure_reason"
    }

    override suspend fun doWork(): Result {
        val file = inputData.getString(KEY_FILENAME)

        try {
            if (file == null || file.isBlank() || !File(file).exists()) {
                Result.failure(
                    workDataOf(
                        KEY_FAILURE_REASON to "invalid file: $file"
                    )
                )
            }

            val fileToUpload = File(file!!)

            apiClient.uploadFile(fileToUpload)

            fileToUpload.delete()

            showNotification()

            return Result.success()
        } catch (error: Error) {
            return if(runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure(
                    workDataOf(
                        KEY_FAILURE_REASON to error.message
                    )
                )
            }
        }
    }

    private suspend fun showNotification() {
        val i = Intent(Intent.ACTION_MAIN)
        val pendingIntentFlag = PendingIntentFlagHelper.getMutableFlag(PendingIntent.FLAG_UPDATE_CURRENT)
        val detailsIntent = PendingIntent.getActivity(applicationContext, 0, i, pendingIntentFlag)

        val builder =
            NotificationCompat.Builder(applicationContext, MawApplication.NOTIFICATION_CHANNEL_ID_UPLOAD_FILES)
                .setSmallIcon(R.drawable.ic_status_notification)
                .setContentTitle("Media Uploaded!")
                .setContentText("File uploaded.  Go to files.mikeandwan.us to manage your files.")
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
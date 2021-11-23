package us.mikeandwan.photos.workers

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import us.mikeandwan.photos.MawApplication
import us.mikeandwan.photos.R
import us.mikeandwan.photos.domain.NotificationPreferenceRepository
import us.mikeandwan.photos.domain.PhotoCategoryRepository
import us.mikeandwan.photos.domain.models.PhotoCategory
import us.mikeandwan.photos.utils.NOTIFICATION_CHANNEL_ID_NEW_CATEGORIES
import us.mikeandwan.photos.utils.PendingIntentFlagHelper

@HiltWorker
class UpdateCategoriesWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val photoCategoryRepository: PhotoCategoryRepository,
    private val preferenceRepository: NotificationPreferenceRepository,
    private val notificationManager: NotificationManager
): CoroutineWorker(appContext, params) {
    companion object {
        const val WORK_NAME = "WORK: update_categories"
    }

    override suspend fun doWork(): Result {
        if(photoCategoryRepository.getYears().first().isEmpty()) {
            return Result.success()
        }

        val newCategories = photoCategoryRepository.getNewCategories().first()

        if(newCategories.isNotEmpty()) {
            showNotification(newCategories)
        }

        return Result.success()
    }

    private suspend fun showNotification(newCategories: List<PhotoCategory>) {
        val i = Intent(Intent.ACTION_MAIN)
        val pluralize = if (newCategories.size == 1) "category" else "categories"
        val contentText = "${newCategories.size} new $pluralize"
        val pendingIntentFlag = PendingIntentFlagHelper.getMutableFlag(PendingIntent.FLAG_UPDATE_CURRENT)
        val detailsIntent = PendingIntent.getActivity(applicationContext, 0, i, pendingIntentFlag)

        val builder =
            NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID_NEW_CATEGORIES)
                .setSmallIcon(R.drawable.ic_status_notification)
                .setContentTitle("New Photos Available")
                .setContentText(contentText)
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
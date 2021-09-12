package us.mikeandwan.photos.services

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import androidx.core.app.NotificationCompat
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import us.mikeandwan.photos.MawApplication
import us.mikeandwan.photos.R
import us.mikeandwan.photos.prefs.NotificationPreference
import us.mikeandwan.photos.ui.login.LoginActivity
import javax.inject.Inject

@AndroidEntryPoint
class UpdateCategoriesJobService : JobService() {
    private val _disposables = CompositeDisposable()
    private val _app: MawApplication? = null

    @Inject lateinit var _dataServices: DataServices
    @Inject lateinit var _notificationPref: NotificationPreference
    @Inject lateinit var _notificationManager: NotificationManager

    override fun onStartJob(params: JobParameters): Boolean {
        Timber.d("Update Categories Job started")

        _disposables.add(Flowable
            .fromCallable { updateCategories() }
            .subscribeOn(Schedulers.io())
            .subscribe(
                { x: Boolean? ->
                    Timber.i("completed updating categories")
                    jobFinished(params, false)
                }
            ) { ex: Throwable ->
                Timber.e("error updating categories: %s", ex.message)
                jobFinished(params, false)
            }
        )

        return true
    }

    override fun onStopJob(params: JobParameters): Boolean {
        Timber.d("Update Categories Job was cancelled before completing.")
        _disposables.clear()

        return false
    }

    private fun updateCategories(): Boolean {
        var totalCount: Int

        try {
            Timber.d("about to get recent categories")
            val categories = _dataServices.recentCategories.items
            totalCount = _app!!.notificationCount + categories.size
            Timber.i("received recent categories; count: %d", totalCount)
            _app.notificationCount = totalCount
        } catch (ex: Exception) {
            Timber.e("Error trying to obtain recent categories: %s", ex.message)
            totalCount = -1
        }

        // force a notification about bad credentials
        if (totalCount < 0 || totalCount > 0 && _notificationPref.doNotify) {
            addNotification(
                totalCount,
                _notificationPref.doVibrate
            )
        }

        return true
    }

    private fun addNotification(count: Int, vibrate: Boolean) {
        val i = Intent(Intent.ACTION_MAIN)
        i.setClass(this, LoginActivity::class.java)
        val title: String
        val contentText: String

        if (count == -1) {
            title = "Authentication Error"
            contentText = "Please update your credentials"
        } else {
            title = "New Photos Available"
            val pluralize = if (count == 1) "category" else "categories"
            contentText = "$count new $pluralize"
        }

        val detailsIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT)
        val builder =
            NotificationCompat.Builder(this, MawApplication.NOTIFICATION_CHANNEL_ID_NEW_CATEGORIES)
                .setSmallIcon(R.drawable.ic_status_notification)
                .setContentTitle(title)
                .setContentText(contentText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(detailsIntent)
                .setAutoCancel(true)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)

        if (vibrate) {
            builder.setVibrate(longArrayOf(300, 300))
        }

        val notification = builder.build()

        _notificationManager.notify(0, notification)
    }
}
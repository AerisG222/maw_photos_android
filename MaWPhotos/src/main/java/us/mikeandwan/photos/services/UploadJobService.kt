package us.mikeandwan.photos.services

import dagger.hilt.android.AndroidEntryPoint
import android.app.job.JobService
import io.reactivex.disposables.CompositeDisposable
import us.mikeandwan.photos.MawApplication
import javax.inject.Inject
import us.mikeandwan.photos.services.DataServices
import us.mikeandwan.photos.prefs.NotificationPreference
import android.app.NotificationManager
import android.app.job.JobParameters
import timber.log.Timber
import io.reactivex.schedulers.Schedulers
import android.content.Intent
import us.mikeandwan.photos.ui.receiver.PhotoReceiverActivity
import android.app.PendingIntent
import android.net.Uri
import androidx.core.app.NotificationCompat
import us.mikeandwan.photos.R
import android.text.TextUtils
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class UploadJobService : JobService() {
    private val _disposables = CompositeDisposable()
    private val _app: MawApplication? = null
    private var _uploadCount = 0

    @Inject lateinit var _dataServices: DataServices
    @Inject lateinit var _notificationPref: NotificationPreference
    @Inject lateinit var _notificationManager: NotificationManager

    override fun onStartJob(params: JobParameters): Boolean {
        Timber.d("Starting upload files job")
        _disposables.add(_dataServices
            .getFileQueueObservable()
            .filter { obj: Array<File?>? -> Objects.nonNull(obj) }
            .debounce(100, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .subscribe(
                { files: Array<File?> ->
                    if (files.size == 0) {
                        alertIfNeeded()
                        jobFinished(params, true)
                    } else {
                        _dataServices!!.uploadQueuedFile(files[0])
                        _uploadCount++
                    }
                }
            ) { ex: Throwable ->
                Timber.e("error uploading files: %s", ex.message)
                alertIfNeeded()
                jobFinished(params, true)
            }
        )
        return true
    }

    override fun onStopJob(params: JobParameters): Boolean {
        Timber.d("Stopping upload files job")
        alertIfNeeded()
        _disposables.clear()
        return true
    }

    private fun alertIfNeeded() {
        if (_uploadCount > 0) {
            addNotification(
                _uploadCount,
                _notificationPref!!.notificationRingtone,
                _notificationPref!!.doVibrate
            )
        }
        _uploadCount = 0
    }

    private fun addNotification(uploadCount: Int, ringtone: String, vibrate: Boolean) {
        val i = Intent(Intent.ACTION_MAIN)
        i.setClass(this, PhotoReceiverActivity::class.java)
        val detailsIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT)
        val builder =
            NotificationCompat.Builder(this, MawApplication.NOTIFICATION_CHANNEL_ID_UPLOAD_FILES)
                .setSmallIcon(R.drawable.ic_status_notification)
                .setContentTitle("Media Uploaded!")
                .setContentText("$uploadCount file(s) uploaded.  Go to mikeandwan.us to manage your files.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(detailsIntent)
                .setAutoCancel(true)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
        if (!TextUtils.isEmpty(ringtone)) {
            builder.setSound(Uri.parse(ringtone))
        }
        if (vibrate) {
            builder.setVibrate(longArrayOf(300, 300))
        }
        val notification = builder.build()
        if (_notificationManager != null) {
            _notificationManager!!.notify(0, notification)
        }
    }
}
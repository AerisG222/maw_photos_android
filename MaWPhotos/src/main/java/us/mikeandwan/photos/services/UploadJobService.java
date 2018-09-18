package us.mikeandwan.photos.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.R;
import us.mikeandwan.photos.prefs.NotificationPreference;
import us.mikeandwan.photos.ui.receiver.PhotoReceiverActivity;


public class UploadJobService extends JobService {
    private final CompositeDisposable _disposables = new CompositeDisposable();
    private MawApplication _app;
    private boolean _isWorking = false;

    @Inject DataServices _dataServices;
    @Inject NotificationPreference _notificationPref;
    @Inject NotificationManager _notificationManager;


    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(MawApplication.LOG_TAG, "Starting upload files job");

        _app = (MawApplication) getApplication();
        _app.getApplicationComponent().inject(this);

        _isWorking = true;

        _disposables.add(
            Flowable.fromCallable(this::uploadFiles)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    x -> {
                        _isWorking = false;
                        jobFinished(params, false);
                    },
                    ex -> {
                        Log.e(MawApplication.LOG_TAG, "error uploading files: " + ex.getMessage());
                        _isWorking = false;
                        jobFinished(params, false);
                    }
                )
        );

        return _isWorking;
    }


    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(MawApplication.LOG_TAG, "Stopping upload files job");

        _disposables.clear();

        boolean needsRescheduling = _isWorking;

        jobFinished(params, needsRescheduling);

        return needsRescheduling;
    }


    private boolean uploadFiles() {
        int uploadedCount = 0;

        try {
            uploadedCount = _dataServices.uploadQueuedFiles();
        } catch (Exception mae) {
            Log.e(MawApplication.LOG_TAG, "Error uploading files: " + mae.getMessage());
        }

        if (uploadedCount > 0) {
            addNotification(_notificationPref.getNotificationRingtone(), _notificationPref.getDoVibrate());
        }

        return true;
    }


    private void addNotification(String ringtone, Boolean vibrate) {
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.setClass(this, PhotoReceiverActivity.class);

        PendingIntent detailsIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, MawApplication.NOTIFICATION_CHANNEL_ID_UPLOAD_FILES)
            .setSmallIcon(R.drawable.ic_status_notification)
            .setContentTitle("Media Upload Complete!")
            .setContentText("Go to mikeandwan.us to manage your files")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(detailsIntent)
            .setAutoCancel(true)
            .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE);

        if (!TextUtils.isEmpty(ringtone)) {
            builder.setSound(Uri.parse(ringtone));
        }

        if (vibrate) {
            builder.setVibrate(new long[]{300, 300});
        }

        Notification notification = builder.build();

        if(_notificationManager != null) {
            _notificationManager.notify(0, notification);
        }
    }
}

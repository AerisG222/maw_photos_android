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

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.R;
import us.mikeandwan.photos.models.Category;
import us.mikeandwan.photos.prefs.NotificationPreference;
import us.mikeandwan.photos.ui.login.LoginActivity;


public class UpdateCategoriesJobService extends JobService {
    private final CompositeDisposable _disposables = new CompositeDisposable();
    private MawApplication _app;
    private boolean _isWorking = false;

    @Inject DataServices _dataServices;
    @Inject NotificationPreference _notificationPref;
    @Inject NotificationManager _notificationManager;


    @Override
    public boolean onStartJob(final JobParameters params) {
        Log.d(MawApplication.LOG_TAG, "Update Categories Job started");

        _app = (MawApplication) getApplication();
        _app.getApplicationComponent().inject(this);

        _isWorking = true;

        _disposables.add(
                Flowable.fromCallable(this::updateCategories)
                        .subscribeOn(Schedulers.io())
                        .subscribe(
                                x -> {
                                    _isWorking = false;
                                    jobFinished(params, false);
                                },
                                ex -> {
                                    Log.e(MawApplication.LOG_TAG, "error updating categories: " + ex.getMessage());
                                    _isWorking = false;
                                    jobFinished(params, false);
                                }
                        )
        );

        return _isWorking;
    }


    @Override
    public boolean onStopJob(final JobParameters params) {
        Log.d(MawApplication.LOG_TAG, "Update Categories Job was cancelled before completing.");

        _disposables.clear();

        boolean needsRescheduling = _isWorking;

        jobFinished(params, needsRescheduling);

        return needsRescheduling;
    }


    private boolean updateCategories() {
        int totalCount;

        try {
            List<Category> categories = _dataServices.getRecentCategories();

            totalCount = _app.getNotificationCount() + categories.size();

            _app.setNotificationCount(totalCount);
        } catch (Exception mae) {
            totalCount = -1;
        }

        // force a notification about bad credentials
        if (totalCount < 0 || (totalCount > 0 && _notificationPref.getDoNotify())) {
            addNotification(totalCount, _notificationPref.getNotificationRingtone(), _notificationPref.getDoVibrate());
        }

        return true;
    }


    private void addNotification(int count, String ringtone, Boolean vibrate) {
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.setClass(this, LoginActivity.class);

        String title;
        String contentText;

        if (count == -1) {
            title = "Authentication Error";
            contentText = "Please update your credentials";
        } else {
            title = "New Photos Available";
            String pluralize = count == 1 ? "category" : "categories";
            contentText = String.valueOf(count) + " new " + pluralize;
        }

        PendingIntent detailsIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, MawApplication.NOTIFICATION_CHANNEL_ID_NEW_CATEGORIES)
                .setSmallIcon(R.drawable.ic_stat_notify)
                .setContentTitle(title)
                .setContentText(contentText)
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

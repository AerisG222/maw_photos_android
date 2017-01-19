package us.mikeandwan.photos.services.poller;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;

import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.R;
import us.mikeandwan.photos.activities.LoginActivity;
import us.mikeandwan.photos.models.Category;
import us.mikeandwan.photos.models.Credentials;
import us.mikeandwan.photos.services.MawDataManager;
import us.mikeandwan.photos.services.MawAuthenticationException;
import us.mikeandwan.photos.services.PhotoApiClient;


public class MawPollerService extends Service {
    private ServiceHandler _serviceHandler;

    @Bean
    PhotoApiClient _client;


    @Override
    public void onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        Looper serviceLooper = thread.getLooper();
        _serviceHandler = new ServiceHandler(serviceLooper);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(MawApplication.LOG_TAG, "MawPollerService.onStartCommand for id " + String.valueOf(startId));

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = _serviceHandler.obtainMessage();
        msg.arg1 = startId;
        _serviceHandler.sendMessage(msg);

        return Service.START_NOT_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        // we don't provide a binding for this service currently
        return null;
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

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle(title);
        builder.setContentText(contentText);
        builder.setSmallIcon(R.drawable.ic_stat_notify);
        builder.setContentIntent(detailsIntent);
        builder.setAutoCancel(true);

        if (!TextUtils.isEmpty(ringtone)) {
            builder.setSound(Uri.parse(ringtone));
        }

        if (vibrate) {
            builder.setVibrate(new long[]{300, 300});
        }

        Notification notification = builder.build();

        NotificationManager mgr = (NotificationManager) getApplication().getSystemService(NOTIFICATION_SERVICE);
        mgr.notify(0, notification);
    }


    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            MawDataManager dm = new MawDataManager(getBaseContext());
            int maxId = dm.getLatestCategoryId();
            int totalCount = 0;

            if (!_client.isConnected()) {
                Log.w(MawApplication.LOG_TAG, "not connected to network, skipping poll check");
                return;
            }

            if (!_client.isAuthenticated()) {
                Credentials creds = dm.getCredentials();

                if (!_client.authenticate(creds.getUsername(), creds.getPassword())) {
                    Log.e(MawApplication.LOG_TAG, "unable to authenticate - will not check for new photos");
                }
            }

            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

            try {
                List<Category> categories = _client.getRecentCategories(maxId);

                if (categories.size() > 0) {
                    for (Category category : categories) {
                        if (!_client.downloadPhoto(category.getTeaserPhotoInfo().getPath())) {
                            Log.d(MawApplication.LOG_TAG, "unable to download teaser: " + category.getTeaserPhotoInfo().getPath());
                        }

                        dm.addCategory(category);
                    }

                    totalCount = MawApplication.getNotificationCount() + categories.size();

                    MawApplication.setNotificationCount(totalCount);
                }
            } catch (MawAuthenticationException mae) {
                totalCount = -1;
            }

            // force a notification about bad credentials
            if (totalCount < 0 || (totalCount > 0 && sharedPrefs.getBoolean("notifications_new_message", true))) {
                String ringtone = sharedPrefs.getString("notifications_new_message_ringtone", "");
                Boolean vibrate = sharedPrefs.getBoolean("notifications_new_message_vibrate", false);

                addNotification(totalCount, ringtone, vibrate);
            }

            Log.d(MawApplication.LOG_TAG, "stopping service for id " + String.valueOf(msg.arg1));

            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            stopSelf(msg.arg1);
        }
    }
}

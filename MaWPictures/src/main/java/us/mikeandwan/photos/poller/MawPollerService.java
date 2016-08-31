package us.mikeandwan.photos.poller;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
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

import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.SystemService;

import java.util.List;

import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.R;
import us.mikeandwan.photos.activities.LoginActivity_;
import us.mikeandwan.photos.data.Category;
import us.mikeandwan.photos.data.Credentials;
import us.mikeandwan.photos.data.MawDataManager;
import us.mikeandwan.photos.services.MawAuthenticationException;
import us.mikeandwan.photos.services.PhotoApiClient;


@SuppressWarnings("ALL")
@SuppressLint("Registered")
@EService
public class MawPollerService extends Service {
    private Context _context;
    private ServiceHandler _serviceHandler;

    @SystemService
    NotificationManager _notificationManager;

    @Override
    public void onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        _context = getApplicationContext();

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
        i.setClass(_context, LoginActivity_.class);

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

        PendingIntent detailsIntent = PendingIntent.getActivity(_context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(_context);
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

        _notificationManager.notify(0, notification);
    }


    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            MawDataManager dm = new MawDataManager(_context);
            int maxId = dm.getLatestCategoryId();
            int totalCount = 0;

            PhotoApiClient client = new PhotoApiClient(_context);

            if (!client.isConnected(_context)) {
                Log.w(MawApplication.LOG_TAG, "not connected to network, skipping poll check");
                return;
            }

            if (!client.isAuthenticated()) {
                Credentials creds = dm.getCredentials();

                if (!client.authenticate(creds.getUsername(), creds.getPassword())) {
                    Log.e(MawApplication.LOG_TAG, "unable to authenticate - will not check for new photos");
                }
            }

            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

            try {
                List<Category> categories = client.getRecentCategories(maxId);

                if (categories.size() > 0) {
                    for (Category category : categories) {
                        if (!client.downloadPhoto(category.getTeaserPhotoInfo().getPath())) {
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

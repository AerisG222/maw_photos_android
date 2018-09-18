package us.mikeandwan.photos.di;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobScheduler;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.os.Build;
import android.preference.PreferenceManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.R;
import us.mikeandwan.photos.services.DataServices;
import us.mikeandwan.photos.services.DatabaseAccessor;
import us.mikeandwan.photos.services.PhotoApiClient;
import us.mikeandwan.photos.services.PhotoStorage;
import us.mikeandwan.photos.services.UpdateCategoriesJobScheduler;
import us.mikeandwan.photos.services.UploadJobScheduler;


@Module
public class ApplicationModule {
    private Application _application;


    public ApplicationModule(Application application) {
        _application = application;
    }


    @Provides
    @Singleton
    Application provideApplication() {
        return _application;
    }


    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences(Application application) {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }


    @Provides
    @Singleton
    JobScheduler provideJobScheduler(Application application) {
        return (JobScheduler) application.getSystemService(Context.JOB_SCHEDULER_SERVICE);
    }


    @Provides
    @Singleton
    NotificationManager provideNotificationManager(Application application) {
        NotificationManager notificationManager = (NotificationManager) application.getSystemService(Application.NOTIFICATION_SERVICE);

        addNewCategoriesNotificationChannel(application, notificationManager);
        addUploadNotificationChannel(application, notificationManager);

        return notificationManager;
    }


    @Provides
    @Singleton
    UpdateCategoriesJobScheduler provideUpdateCategoriesJobScheduler(Application application, JobScheduler jobScheduler) {
        return new UpdateCategoriesJobScheduler(application, jobScheduler);
    }


    @Provides
    @Singleton
    UploadJobScheduler provideUploadScheduler(Application application, JobScheduler jobScheduler) {
        return new UploadJobScheduler(application, jobScheduler);
    }


    @Provides
    @Singleton
    DataServices provideDataServices(DatabaseAccessor databaseAccessor,
                                     PhotoApiClient photoApiClient,
                                     PhotoStorage photoStorage) {
        return new DataServices(databaseAccessor, photoApiClient, photoStorage);
    }


    private void addNewCategoriesNotificationChannel(Application application, NotificationManager notificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = application.getString(R.string.channel_name_new_categories);
            String description = application.getString(R.string.channel_description_new_categories);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build();
            NotificationChannel channel = new NotificationChannel(MawApplication.NOTIFICATION_CHANNEL_ID_NEW_CATEGORIES, name, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(description);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{300, 300});
            channel.setLightColor(Color.argb(255, 75, 0, 130));
            channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), audioAttributes);

            if (notificationManager != null) {
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                notificationManager.createNotificationChannel(channel);
            }
        }
    }


    private void addUploadNotificationChannel(Application application, NotificationManager notificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = application.getString(R.string.channel_name_upload);
            String description = application.getString(R.string.channel_description_upload);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build();
            NotificationChannel channel = new NotificationChannel(MawApplication.NOTIFICATION_CHANNEL_ID_UPLOAD_FILES, name, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(description);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{300, 300});
            channel.setLightColor(Color.argb(255, 75, 0, 130));
            channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), audioAttributes);

            if (notificationManager != null) {
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}

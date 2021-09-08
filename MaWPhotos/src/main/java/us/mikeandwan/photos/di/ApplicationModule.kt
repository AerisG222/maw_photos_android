package us.mikeandwan.photos.di

import android.app.Application
import javax.inject.Singleton
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.app.job.JobScheduler
import android.app.NotificationManager
import us.mikeandwan.photos.services.UpdateCategoriesJobScheduler
import us.mikeandwan.photos.services.UploadJobScheduler
import us.mikeandwan.photos.services.DatabaseAccessor
import us.mikeandwan.photos.services.PhotoApiClient
import us.mikeandwan.photos.services.PhotoStorage
import us.mikeandwan.photos.services.DataServices
import android.os.Build
import us.mikeandwan.photos.R
import android.media.AudioAttributes
import android.app.NotificationChannel
import android.content.Context
import android.graphics.Color
import us.mikeandwan.photos.MawApplication
import android.media.RingtoneManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {
    @Provides
    @Singleton
    fun provideSharedPreferences(app: Application): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(app)
    }

    @Provides
    @Singleton
    fun provideJobScheduler(app: Application): JobScheduler {
        return app.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
    }

    @Provides
    @Singleton
    fun provideNotificationManager(app: Application): NotificationManager {
        val notificationManager =
            app.getSystemService(Application.NOTIFICATION_SERVICE) as NotificationManager
        addNewCategoriesNotificationChannel(app, notificationManager)
        addUploadNotificationChannel(app, notificationManager)
        return notificationManager
    }

    @Provides
    @Singleton
    fun provideUpdateCategoriesJobScheduler(
        app: Application,
        jobScheduler: JobScheduler?
    ): UpdateCategoriesJobScheduler {
        return UpdateCategoriesJobScheduler(app, jobScheduler)
    }

    @Provides
    @Singleton
    fun provideUploadScheduler(
        app: Application,
        jobScheduler: JobScheduler?
    ): UploadJobScheduler {
        return UploadJobScheduler(app, jobScheduler)
    }

    @Provides
    @Singleton
    fun provideDataServices(
        databaseAccessor: DatabaseAccessor?,
        photoApiClient: PhotoApiClient?,
        photoStorage: PhotoStorage?
    ): DataServices {
        return DataServices(databaseAccessor, photoApiClient, photoStorage)
    }

    private fun addNewCategoriesNotificationChannel(
        app: Application,
        notificationManager: NotificationManager?
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = app.getString(R.string.channel_name_new_categories)
            val description = app.getString(R.string.channel_description_new_categories)
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()
            val channel = NotificationChannel(
                MawApplication.NOTIFICATION_CHANNEL_ID_NEW_CATEGORIES,
                name,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = description
            channel.enableLights(true)
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(300, 300)
            channel.lightColor = Color.argb(255, 75, 0, 130)
            channel.setSound(
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                audioAttributes
            )
            notificationManager?.createNotificationChannel(channel)
        }
    }

    private fun addUploadNotificationChannel(
        app: Application,
        notificationManager: NotificationManager?
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = app.getString(R.string.channel_name_upload)
            val description = app.getString(R.string.channel_description_upload)
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()
            val channel = NotificationChannel(
                MawApplication.NOTIFICATION_CHANNEL_ID_UPLOAD_FILES,
                name,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = description
            channel.enableLights(true)
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(300, 300)
            channel.lightColor = Color.argb(255, 75, 0, 130)
            channel.setSound(
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                audioAttributes
            )
            notificationManager?.createNotificationChannel(channel)
        }
    }
}
package us.mikeandwan.photos.di

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import us.mikeandwan.photos.MawApplication
import us.mikeandwan.photos.R
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {
    @Provides
    @Singleton
    fun provideNotificationManager(app: Application): NotificationManager {
        val notificationManager = app.getSystemService(Application.NOTIFICATION_SERVICE) as NotificationManager

        addNewCategoriesNotificationChannel(app, notificationManager)
        addUploadNotificationChannel(app, notificationManager)

        return notificationManager
    }

    private fun addNewCategoriesNotificationChannel(
        app: Application,
        notificationManager: NotificationManager
    ) {
        val name = app.getString(R.string.channel_name_new_categories)
        val description = app.getString(R.string.channel_description_new_categories)

        createChannel(notificationManager, name, description, MawApplication.NOTIFICATION_CHANNEL_ID_NEW_CATEGORIES)
    }

    private fun addUploadNotificationChannel(
        app: Application,
        notificationManager: NotificationManager
    ) {
        val name = app.getString(R.string.channel_name_upload)
        val description = app.getString(R.string.channel_description_upload)

        createChannel(notificationManager, name, description, MawApplication.NOTIFICATION_CHANNEL_ID_UPLOAD_FILES)
    }

    private fun createChannel(notificationManager: NotificationManager, name: String, description: String, channelId: String) {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .build()

        val channel = NotificationChannel(
            channelId,
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

        notificationManager.createNotificationChannel(channel)
    }
}
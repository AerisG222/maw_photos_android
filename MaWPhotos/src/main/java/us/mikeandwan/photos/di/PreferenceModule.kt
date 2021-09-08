package us.mikeandwan.photos.di

import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import us.mikeandwan.photos.prefs.CategoryDisplayPreference
import us.mikeandwan.photos.prefs.NotificationPreference
import us.mikeandwan.photos.prefs.PhotoDisplayPreference
import us.mikeandwan.photos.prefs.SyncPreference
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class PreferenceModule {
    @Provides
    @Singleton
    fun provideCategoryDisplayPreference(sharedPrefs: SharedPreferences?): CategoryDisplayPreference {
        return CategoryDisplayPreference(sharedPrefs)
    }

    @Provides
    @Singleton
    fun provideNotificationPreference(sharedPrefs: SharedPreferences?): NotificationPreference {
        return NotificationPreference(sharedPrefs)
    }

    @Provides
    @Singleton
    fun providePhotoDisplayPreference(sharedPrefs: SharedPreferences?): PhotoDisplayPreference {
        return PhotoDisplayPreference(sharedPrefs)
    }

    @Provides
    @Singleton
    fun provideSyncPreference(sharedPrefs: SharedPreferences?): SyncPreference {
        return SyncPreference(sharedPrefs)
    }
}
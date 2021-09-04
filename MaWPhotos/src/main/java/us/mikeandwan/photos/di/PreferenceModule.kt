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

@Module
@InstallIn(SingletonComponent::class)
class PreferenceModule {
    @Provides
    fun provideCategoryDisplayPreference(sharedPrefs: SharedPreferences?): CategoryDisplayPreference {
        return CategoryDisplayPreference(sharedPrefs)
    }

    @Provides
    fun provideNotificationPreference(sharedPrefs: SharedPreferences?): NotificationPreference {
        return NotificationPreference(sharedPrefs)
    }

    @Provides
    fun providePhotoDisplayPreference(sharedPrefs: SharedPreferences?): PhotoDisplayPreference {
        return PhotoDisplayPreference(sharedPrefs)
    }

    @Provides
    fun provideSyncPreference(sharedPrefs: SharedPreferences?): SyncPreference {
        return SyncPreference(sharedPrefs)
    }
}
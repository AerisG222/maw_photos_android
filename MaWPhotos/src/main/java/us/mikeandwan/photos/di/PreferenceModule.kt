package us.mikeandwan.photos.di

import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import us.mikeandwan.photos.preferences.CategoryDisplayPreference
import us.mikeandwan.photos.preferences.NotificationPreference
import us.mikeandwan.photos.preferences.PhotoDisplayPreference
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class PreferenceModule {
    @Provides
    @Singleton
    fun provideCategoryDisplayPreference(sharedPrefs: SharedPreferences): CategoryDisplayPreference {
        return CategoryDisplayPreference(sharedPrefs)
    }

    @Provides
    @Singleton
    fun provideNotificationPreference(sharedPrefs: SharedPreferences): NotificationPreference {
        return NotificationPreference(sharedPrefs)
    }

    @Provides
    @Singleton
    fun providePhotoDisplayPreference(sharedPrefs: SharedPreferences): PhotoDisplayPreference {
        return PhotoDisplayPreference(sharedPrefs)
    }
}
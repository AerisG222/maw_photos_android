package us.mikeandwan.photos.di;

import android.content.SharedPreferences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import us.mikeandwan.photos.prefs.CategoryDisplayPreference;
import us.mikeandwan.photos.prefs.NotificationPreference;
import us.mikeandwan.photos.prefs.PhotoDisplayPreference;
import us.mikeandwan.photos.prefs.SyncPreference;


@Module
public class PreferenceModule {
    @Provides
    @Singleton
    public CategoryDisplayPreference provideCategoryDisplayPreference(SharedPreferences sharedPrefs) {
        return new CategoryDisplayPreference(sharedPrefs);
    }


    @Provides
    @Singleton
    public NotificationPreference provideNotificationPreference(SharedPreferences sharedPrefs) {
        return new NotificationPreference(sharedPrefs);
    }


    @Provides
    @Singleton
    public PhotoDisplayPreference providePhotoDisplayPreference(SharedPreferences sharedPrefs) {
        return new PhotoDisplayPreference(sharedPrefs);
    }


    @Provides
    @Singleton
    public SyncPreference provideSyncPreference(SharedPreferences sharedPrefs) {
        return new SyncPreference(sharedPrefs);
    }
}

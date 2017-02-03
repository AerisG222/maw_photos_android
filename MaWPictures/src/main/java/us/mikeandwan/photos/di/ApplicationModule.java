package us.mikeandwan.photos.di;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;


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
}

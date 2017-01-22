package us.mikeandwan.photos.di;

import android.app.Application;

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
}

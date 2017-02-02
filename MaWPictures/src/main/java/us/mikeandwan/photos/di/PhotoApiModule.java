package us.mikeandwan.photos.di;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import us.mikeandwan.photos.services.AuthenticationExceptionHandler;
import us.mikeandwan.photos.services.MawDataManager;
import us.mikeandwan.photos.services.PhotoApiClient;
import us.mikeandwan.photos.services.PhotoStorage;


@Module
public class PhotoApiModule {
    @Provides
    @Singleton
    public PhotoApiClient providePhotoApiClient(Application application, PhotoStorage photoStorage, MawDataManager dataManager) {
        return new PhotoApiClient(application, photoStorage, dataManager);
    }


    @Provides
    @Singleton
    public AuthenticationExceptionHandler provideAuthenticationExceptionHandler(Application application) {
        return new AuthenticationExceptionHandler(application);
    }
}

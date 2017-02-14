package us.mikeandwan.photos.di;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import us.mikeandwan.photos.Constants;
import us.mikeandwan.photos.services.AuthenticationExceptionHandler;
import us.mikeandwan.photos.services.MawDataManager;
import us.mikeandwan.photos.services.PhotoApiClient;
import us.mikeandwan.photos.services.PhotoApiCookieJar;
import us.mikeandwan.photos.services.PhotoStorage;


@Module
public class PhotoApiModule {
    @Provides
    @Singleton
    PhotoApiCookieJar provideCookieJar() {
        return new PhotoApiCookieJar();
    }


    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(PhotoApiCookieJar cookieJar) {
        return new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .build();
    }


    @Provides
    @Singleton
    Retrofit provideRetrofit(OkHttpClient httpClient) {
        return new Retrofit
                .Builder()
                .baseUrl(Constants.SITE_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .client(httpClient)
                .build();
    }


    @Provides
    @Singleton
    PhotoApiClient providePhotoApiClient(Application application,
                                         PhotoStorage photoStorage,
                                         MawDataManager dataManager,
                                         OkHttpClient httpClient,
                                         Retrofit retrofit,
                                         PhotoApiCookieJar cookieJar) {
        return new PhotoApiClient(application, photoStorage, dataManager, httpClient, retrofit, cookieJar);
    }


    @Provides
    @Singleton
    AuthenticationExceptionHandler provideAuthenticationExceptionHandler(Application application) {
        return new AuthenticationExceptionHandler(application);
    }
}

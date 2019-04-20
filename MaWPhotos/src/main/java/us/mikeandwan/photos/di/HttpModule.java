package us.mikeandwan.photos.di;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import us.mikeandwan.photos.services.AuthAuthenticator;
import us.mikeandwan.photos.services.AuthInterceptor;


@Module
public class HttpModule {
    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(AuthAuthenticator authenticator, AuthInterceptor authInterceptor) {
        return new OkHttpClient
            .Builder()
            .authenticator(authenticator)
            .addInterceptor(authInterceptor)
            .build();
    }
}

package us.mikeandwan.photos.di;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import us.mikeandwan.photos.services.AuthInterceptor;


@Module
public class HttpModule {
    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(AuthInterceptor authInterceptor) {
        return new OkHttpClient
            .Builder()
            .addNetworkInterceptor(authInterceptor)
            .build();
    }
}

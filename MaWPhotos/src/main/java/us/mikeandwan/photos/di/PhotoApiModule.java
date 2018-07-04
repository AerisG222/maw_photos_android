package us.mikeandwan.photos.di;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import us.mikeandwan.photos.Constants;
import us.mikeandwan.photos.services.PhotoApiClient;


@Module
public class PhotoApiModule {
    @Provides
    @Singleton
    Retrofit provideRetrofit(OkHttpClient httpClient) {
        return new Retrofit
                .Builder()
                .baseUrl(Constants.API_BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .client(httpClient)
                .build();
    }


    @Provides
    @Singleton
    PhotoApiClient providePhotoApiClient(OkHttpClient httpClient,
                                         Retrofit retrofit) {
        return new PhotoApiClient(httpClient, retrofit);
    }
}

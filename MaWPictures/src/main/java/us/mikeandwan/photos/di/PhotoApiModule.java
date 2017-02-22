package us.mikeandwan.photos.di;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import us.mikeandwan.photos.Constants;
import us.mikeandwan.photos.services.AuthenticationExceptionHandler;
import us.mikeandwan.photos.services.PhotoApiClient;
import us.mikeandwan.photos.services.PhotoApiCookieJar;


@Module
public class PhotoApiModule {
    private static final String XSRF_HEADER = "X-XSRF-TOKEN";


    @Provides
    @Singleton
    PhotoApiCookieJar provideCookieJar() {
        return new PhotoApiCookieJar();
    }


    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(PhotoApiCookieJar cookieJar) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .cookieJar(cookieJar);

        builder.networkInterceptors().add(chain -> {
            Request srcRequest = chain.request();
            String xsrfToken = cookieJar.getXsrfToken();

            if(xsrfToken == null) {
                return chain.proceed(srcRequest);
            }

            Request request = srcRequest.newBuilder()
                    .addHeader(XSRF_HEADER, xsrfToken)
                    .build();

            return chain.proceed(request);
        });

        return builder.build();
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
    PhotoApiClient providePhotoApiClient(OkHttpClient httpClient,
                                         Retrofit retrofit,
                                         PhotoApiCookieJar cookieJar) {
        return new PhotoApiClient(httpClient, retrofit, cookieJar);
    }


    @Provides
    @Singleton
    AuthenticationExceptionHandler provideAuthenticationExceptionHandler(Application application) {
        return new AuthenticationExceptionHandler(application);
    }
}

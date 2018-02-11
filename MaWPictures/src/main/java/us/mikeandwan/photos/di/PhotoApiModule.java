package us.mikeandwan.photos.di;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import net.openid.appauth.AuthorizationServiceConfiguration;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.inject.Singleton;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import us.mikeandwan.photos.Constants;
import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.services.AuthInterceptor;
import us.mikeandwan.photos.services.AuthStateManager;
import us.mikeandwan.photos.services.AuthenticationExceptionHandler;
import us.mikeandwan.photos.services.PhotoApiClient;


@Module
public class PhotoApiModule {
    @Provides
    @Singleton
    AuthInterceptor provideAuthInterceptor(AuthStateManager authStateManager) {
        return new AuthInterceptor(authStateManager);
    }


    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(Application application, AuthInterceptor authInterceptor) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        boolean allowUntrusted = true;

        // https://stackoverflow.com/questions/23103174/does-okhttp-support-accepting-self-signed-ssl-certs
        if (allowUntrusted) {
            try {
                Log.w(MawApplication.LOG_TAG, "**** Allow untrusted SSL connection ****");

                final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }

                    @Override
                    public void checkServerTrusted(final X509Certificate[] chain,
                                                   final String authType) throws CertificateException {
                    }

                    @Override
                    public void checkClientTrusted(final X509Certificate[] chain,
                                                   final String authType) throws CertificateException {
                    }
                }};

                SSLContext sslContext = SSLContext.getInstance("SSL");

                sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
                builder.sslSocketFactory(sslContext.getSocketFactory());

                HostnameVerifier hostnameVerifier = (hostname, session) -> {
                    Log.d(MawApplication.LOG_TAG, "Trust Host :" + hostname);
                    return true;
                };

                builder.hostnameVerifier(hostnameVerifier);
            }
            catch(Exception e) {
                Log.e(MawApplication.LOG_TAG, "Error setting up allowance for self signed certs: " + e.getMessage());
            }
        }

        return builder
            .addNetworkInterceptor(authInterceptor)
            .build();
    }


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


    @Provides
    @Singleton
    AuthenticationExceptionHandler provideAuthenticationExceptionHandler(Application application) {
        return new AuthenticationExceptionHandler(application);
    }


    @Provides
    @Singleton
    AuthorizationServiceConfiguration provideAuthorizationServiceConfiguration() {
        return new AuthorizationServiceConfiguration(
            Uri.parse(Constants.AUTH_BASE_URL + "connect/authorize"), // authorization endpoint
            Uri.parse(Constants.AUTH_BASE_URL + "connect/token")); // token endpoint
    }


    @Provides
    @Singleton
    AuthStateManager provideAuthStateManager(Application application) {
        return AuthStateManager.getInstance(application);
    }
}

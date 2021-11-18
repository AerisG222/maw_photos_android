package us.mikeandwan.photos.di

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import us.mikeandwan.photos.Constants
import us.mikeandwan.photos.api.PhotoApiClient
import us.mikeandwan.photos.authorization.AuthAuthenticator
import us.mikeandwan.photos.authorization.AuthInterceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    @Provides
    @Singleton
    fun provideOkHttpClient(
        authenticator: AuthAuthenticator,
        authInterceptor: AuthInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .authenticator(authenticator)
            .addInterceptor(authInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(httpClient: OkHttpClient): Retrofit {
        val mapper = ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

        return Retrofit.Builder()
            .baseUrl(Constants.API_BASE_URL)
            .addConverterFactory(JacksonConverterFactory.create(mapper))
            .client(httpClient)
            .build()
    }

    @Provides
    @Singleton
    fun providePhotoApiClient(retrofit: Retrofit): PhotoApiClient {
        return PhotoApiClient(retrofit)
    }
}
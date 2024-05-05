package us.mikeandwan.photos.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import us.mikeandwan.photos.Constants
import us.mikeandwan.photos.api.PhotoApiClient
import us.mikeandwan.photos.authorization.AuthAuthenticator
import us.mikeandwan.photos.authorization.AuthInterceptor
import javax.inject.Singleton
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.converter.kotlinx.serialization.asConverterFactory

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

    private val json = Json { ignoreUnknownKeys = true }

    @Provides
    @Singleton
    fun provideRetrofit(httpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.API_BASE_URL)
            .addConverterFactory(
                json.asConverterFactory("application/json; charset=utf-8".toMediaType()))
            .client(httpClient)
            .build()
    }

    @Provides
    @Singleton
    fun providePhotoApiClient(retrofit: Retrofit): PhotoApiClient {
        return PhotoApiClient(retrofit)
    }
}

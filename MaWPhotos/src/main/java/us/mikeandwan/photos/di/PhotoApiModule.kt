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
import us.mikeandwan.photos.services.PhotoApiClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class PhotoApiModule {
    @Provides
    @Singleton
    fun provideRetrofit(httpClient: OkHttpClient?): Retrofit {
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
    fun providePhotoApiClient(
        httpClient: OkHttpClient?,
        retrofit: Retrofit?
    ): PhotoApiClient {
        return PhotoApiClient(httpClient, retrofit)
    }
}
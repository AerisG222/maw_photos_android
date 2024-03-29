package us.mikeandwan.photos.di

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.Protocol
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
        // NOTE: the protocols line was useful when testing locally, but ideally should not be used in prod
        // this fixed an error testing locally where kestrel would reset the http2 connection when access token expired.
        // this resulted in OkHttp bailing and not trying the authenticator to get a new token.  changing to 1.1 seemed
        // to work better and avoid this issue when testing locally.
        return OkHttpClient.Builder()
            //.protocols(listOf(Protocol.HTTP_1_1))
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
package us.mikeandwan.photos.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import us.mikeandwan.photos.services.AuthAuthenticator
import us.mikeandwan.photos.services.AuthInterceptor
import okhttp3.OkHttpClient

@Module
@InstallIn(SingletonComponent::class)
class HttpModule {
    @Provides
    fun provideOkHttpClient(
        authenticator: AuthAuthenticator?,
        authInterceptor: AuthInterceptor?
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .authenticator(authenticator)
            .addInterceptor(authInterceptor)
            .build()
    }
}
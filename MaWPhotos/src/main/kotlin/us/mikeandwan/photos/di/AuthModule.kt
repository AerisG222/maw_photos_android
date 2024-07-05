package us.mikeandwan.photos.di

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.openid.appauth.AuthorizationService
import us.mikeandwan.photos.authorization.AuthAuthenticator
import us.mikeandwan.photos.authorization.AuthInterceptor
import us.mikeandwan.photos.authorization.AuthService
import us.mikeandwan.photos.database.AuthorizationDao
import us.mikeandwan.photos.domain.AuthorizationRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {
    @Provides
    @Singleton
    fun provideAuthService(
        application: Application,
        authorizationService: AuthorizationService,
        authorizationRepository: AuthorizationRepository
    ): AuthService =
        AuthService(application, authorizationService, authorizationRepository)

    @Provides
    @Singleton
    fun provideAuthorizationRepository(authorizationDao: AuthorizationDao): AuthorizationRepository =
        AuthorizationRepository(authorizationDao)

    @Provides
    @Singleton
    fun provideAuthorizationService(application: Application): AuthorizationService =
        AuthorizationService(application)

    @Provides
    @Singleton
    fun provideAuthAuthenticator(
        authService: AuthService,
        authorizationService: AuthorizationService,
        authorizationRepository: AuthorizationRepository
    ): AuthAuthenticator =
        AuthAuthenticator(authService, authorizationService, authorizationRepository)

    @Provides
    @Singleton
    fun provideAuthInterceptor(authorizationRepository: AuthorizationRepository): AuthInterceptor =
        AuthInterceptor(authorizationRepository)
}

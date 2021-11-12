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
import us.mikeandwan.photos.authorization.AuthStateManager
import us.mikeandwan.photos.database.AuthorizationDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AuthModule {
    @Provides
    @Singleton
    fun provideAuthService(application: Application, authorizationService: AuthorizationService, authStateManager: AuthStateManager): AuthService {
        return AuthService(application, authorizationService, authStateManager)
    }

    @Provides
    @Singleton
    fun provideAuthStateManager(authorizationDao: AuthorizationDao): AuthStateManager {
        return AuthStateManager(authorizationDao)
    }

    @Provides
    @Singleton
    fun provideAuthorizationService(application: Application): AuthorizationService {
        return AuthorizationService(application)
    }

    @Provides
    @Singleton
    fun provideAuthAuthenticator(
        authService: AuthorizationService,
        authStateManager: AuthStateManager
    ): AuthAuthenticator {
        return AuthAuthenticator(authService, authStateManager)
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(authStateManager: AuthStateManager): AuthInterceptor {
        return AuthInterceptor(authStateManager)
    }
}
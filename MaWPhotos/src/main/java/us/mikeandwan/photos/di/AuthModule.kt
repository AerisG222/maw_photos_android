package us.mikeandwan.photos.di

import android.app.Application
import android.net.Uri
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import timber.log.Timber
import us.mikeandwan.photos.Constants
import us.mikeandwan.photos.authorization.AuthAuthenticator
import us.mikeandwan.photos.authorization.AuthInterceptor
import us.mikeandwan.photos.authorization.AuthService
import us.mikeandwan.photos.authorization.AuthStateManager
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
    fun provideAuthStateManager(application: Application): AuthStateManager {
        return AuthStateManager.getInstance(application)
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

    // http://frogermcs.github.io/async-injection-in-dagger-2-with-rxjava/
    // https://proandroiddev.com/dagger-2-part-three-new-possibilities-3daff12f7ebf
    @Provides
    @Singleton
    fun provideAuthorizationServiceConfigurationObservable(): Observable<AuthorizationServiceConfiguration?> {
        // we pull in the builder above for dev - so that it can initialize the ssl bits to allow self signed certs
        return Observable.create {
            emitter: ObservableEmitter<AuthorizationServiceConfiguration?> ->
                AuthorizationServiceConfiguration.fetchFromIssuer(
                    Uri.parse(Constants.AUTH_BASE_URL)
                ) { serviceConfiguration: AuthorizationServiceConfiguration?, ex: AuthorizationException? ->
                    if (ex != null) {
                        Timber.e("failed to fetch openidc configuration")
                        emitter.onError(ex)
                    }
                    if (serviceConfiguration != null) {
                        emitter.onNext(serviceConfiguration)
                    }
                    emitter.onComplete()
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}
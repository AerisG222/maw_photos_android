package us.mikeandwan.photos.di

import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import android.app.Application
import android.net.Uri
import dagger.Module
import dagger.Provides
import io.reactivex.Observable
import us.mikeandwan.photos.services.AuthStateManager
import net.openid.appauth.AuthorizationService
import us.mikeandwan.photos.services.AuthAuthenticator
import us.mikeandwan.photos.services.AuthInterceptor
import net.openid.appauth.AuthorizationServiceConfiguration
import io.reactivex.ObservableOnSubscribe
import io.reactivex.ObservableEmitter
import net.openid.appauth.AuthorizationServiceConfiguration.RetrieveConfigurationCallback
import net.openid.appauth.AuthorizationException
import timber.log.Timber
import io.reactivex.schedulers.Schedulers
import io.reactivex.android.schedulers.AndroidSchedulers
import us.mikeandwan.photos.Constants

@Module
@InstallIn(SingletonComponent::class)
class AuthModule {
    @Provides
    fun provideAuthStateManager(application: Application?): AuthStateManager {
        return AuthStateManager.getInstance(application!!)
    }

    @Provides
    fun provideAuthorizationService(application: Application?): AuthorizationService {
        return AuthorizationService(application!!)
    }

    @Provides
    fun provideAuthAuthenticator(
        authService: AuthorizationService?,
        authStateManager: AuthStateManager?
    ): AuthAuthenticator {
        return AuthAuthenticator(authService, authStateManager)
    }

    @Provides
    fun provideAuthInterceptor(authStateManager: AuthStateManager?): AuthInterceptor {
        return AuthInterceptor(authStateManager)
    }

    // http://frogermcs.github.io/async-injection-in-dagger-2-with-rxjava/
    // https://proandroiddev.com/dagger-2-part-three-new-possibilities-3daff12f7ebf
    @Provides
    fun provideAuthorizationServiceConfigurationObservable(): Observable<AuthorizationServiceConfiguration?> {
        // we pull in the builder above for dev - so that it can initialize the ssl bits to allow self signed certs
        return Observable.create(
            ObservableOnSubscribe { emitter: ObservableEmitter<AuthorizationServiceConfiguration?> ->
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
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}
package us.mikeandwan.photos.di;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import us.mikeandwan.photos.Constants;
import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.services.AuthAuthenticator;
import us.mikeandwan.photos.services.AuthInterceptor;
import us.mikeandwan.photos.services.AuthStateManager;


@Module
public class AuthModule {
    @Provides
    @Singleton
    AuthStateManager provideAuthStateManager(Application application) {
        return AuthStateManager.getInstance(application);
    }


    @Provides
    @Singleton
    AuthorizationService provideAuthorizationService(Application application) {
        return new AuthorizationService(application);
    }


    @Provides
    @Singleton
    AuthAuthenticator provideAuthAuthenticator(AuthorizationService authService,
                                               AuthStateManager authStateManager) {
        return new AuthAuthenticator(authService, authStateManager);
    }


    @Provides
    @Singleton
    AuthInterceptor provideAuthInterceptor(AuthStateManager authStateManager) {
        return new AuthInterceptor(authStateManager);
    }


    // http://frogermcs.github.io/async-injection-in-dagger-2-with-rxjava/
    // https://proandroiddev.com/dagger-2-part-three-new-possibilities-3daff12f7ebf
    @Provides
    @Singleton
    Observable<AuthorizationServiceConfiguration> provideAuthorizationServiceConfigurationObservable() {
        // we pull in the builder above for dev - so that it can initialize the ssl bits to allow self signed certs
        return Observable.create((ObservableOnSubscribe<AuthorizationServiceConfiguration>) emitter ->
            AuthorizationServiceConfiguration.fetchFromIssuer(
                Uri.parse(Constants.AUTH_BASE_URL),
                (serviceConfiguration, ex) -> {
                    if (ex != null) {
                        Log.e(MawApplication.LOG_TAG, "failed to fetch openidc configuration");
                        emitter.onError(ex);
                    }

                    if(serviceConfiguration != null) {
                        emitter.onNext(serviceConfiguration);
                    }

                    emitter.onComplete();
                }
            )
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }
}

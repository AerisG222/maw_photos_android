package us.mikeandwan.photos.di;

import android.app.Activity;

import net.openid.appauth.AppAuthConfiguration;
import net.openid.appauth.AuthorizationService;

import dagger.Module;
import dagger.Provides;


@Module
public class ActivityModule {
    private final Activity _activity;


    public ActivityModule(Activity activity) {
        this._activity = activity;
    }


    @Provides
    @PerActivity
    Activity provideActivity() {
        return this._activity;
    }


    @Provides
    @PerActivity
    AuthorizationService provideAuthorizationService(Activity activity) {
        AppAuthConfiguration appAuthConfig = new AppAuthConfiguration.Builder()
            .build();

        return new AuthorizationService(activity, appAuthConfig);
    }
}

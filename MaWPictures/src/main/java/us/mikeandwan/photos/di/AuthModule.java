package us.mikeandwan.photos.di;

import android.app.Activity;

import net.openid.appauth.AppAuthConfiguration;
import net.openid.appauth.AuthorizationService;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;


@Module
public class AuthModule {
    @Provides
    @PerActivity
    OkHttpConnectionBuilder provideOkHttpConnectionBuilder(OkHttpClient httpClient) {
        return new OkHttpConnectionBuilder(httpClient);
    }


    @Provides
    @PerActivity
    AuthorizationService provideAuthorizationService(Activity activity, OkHttpConnectionBuilder builder) {
        AppAuthConfiguration appAuthConfig = new AppAuthConfiguration.Builder()
            .setConnectionBuilder(builder)
            .build();

        return new AuthorizationService(activity, appAuthConfig);
    }
}

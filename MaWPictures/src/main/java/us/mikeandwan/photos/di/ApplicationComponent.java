package us.mikeandwan.photos.di;

import android.app.Application;

import net.openid.appauth.AppAuthConfiguration;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;

import javax.inject.Singleton;

import dagger.Component;
import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.prefs.CategoryDisplayPreference;
import us.mikeandwan.photos.prefs.NotificationPreference;
import us.mikeandwan.photos.prefs.PhotoDisplayPreference;
import us.mikeandwan.photos.prefs.SyncPreference;
import us.mikeandwan.photos.services.AuthInterceptor;
import us.mikeandwan.photos.services.AuthStateManager;
import us.mikeandwan.photos.services.AuthenticationExceptionHandler;
import us.mikeandwan.photos.services.DataServices;
import us.mikeandwan.photos.services.DatabaseAccessor;
import us.mikeandwan.photos.services.MawSQLiteOpenHelper;
import us.mikeandwan.photos.services.PhotoApiClient;
import us.mikeandwan.photos.services.PhotoStorage;
import us.mikeandwan.photos.services.poller.MawPollerService;
import us.mikeandwan.photos.services.poller.MawScheduleReceiver;
import us.mikeandwan.photos.ui.BaseActivity;
import us.mikeandwan.photos.ui.settings.SettingsActivity;


@Singleton
@Component(modules = {ApplicationModule.class, HttpModule.class, AuthModule.class, DataStorageModule.class, PhotoApiModule.class, PreferenceModule.class})
public interface ApplicationComponent {
    // identify which services will be available to dependent components
    Application application();
    CategoryDisplayPreference categoryDisplayPreference();
    NotificationPreference notificationPreference();
    PhotoDisplayPreference photoDisplayPreference();
    SyncPreference syncPreference();
    DataServices dataServices();
    PhotoStorage photoStorage();
    DatabaseAccessor databaseAccessor();
    PhotoApiClient photoApiClient();
    AuthenticationExceptionHandler authenticationExceptionHandler();
    Observable<AuthorizationServiceConfiguration> authorizationServiceConfiguration();
    AuthStateManager authStateManager();
    OkHttpClient okHttpClient();
    AuthorizationService authorizationService();
    AuthInterceptor authInterceptor();
    AppAuthConfiguration appAuthConfiguration();

    void inject(MawApplication application);

    // activities
    void inject(BaseActivity activity);
    void inject(SettingsActivity activity);

    // services
    void inject(DatabaseAccessor databaseAccessor);
    void inject(MawSQLiteOpenHelper sqliteOpenHelper);
    void inject(OkHttpClient okHttpClient);
    void inject(PhotoApiClient client);
    void inject(PhotoStorage photoStorage);
    void inject(MawPollerService service);
    void inject(MawScheduleReceiver scheduleReceiver);
    void inject(AuthenticationExceptionHandler authenticationExceptionHandler);
    void inject(AuthorizationServiceConfiguration authorizationServiceConfiguration);
    void inject(AuthStateManager authStateManager);
    void inject(AuthorizationService authorizationService);
    void inject(AuthInterceptor authInterceptor);
    void inject(AppAuthConfiguration appAuthConfiguration);
}

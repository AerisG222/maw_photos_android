package us.mikeandwan.photos.di;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Component;
import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.prefs.CategoryDisplayPreference;
import us.mikeandwan.photos.prefs.NotificationPreference;
import us.mikeandwan.photos.prefs.PhotoDisplayPreference;
import us.mikeandwan.photos.prefs.SyncPreference;
import us.mikeandwan.photos.ui.BaseActivity;
import us.mikeandwan.photos.ui.settings.SettingsActivity;
import us.mikeandwan.photos.services.AuthenticationExceptionHandler;
import us.mikeandwan.photos.services.MawDataManager;
import us.mikeandwan.photos.services.MawSQLiteOpenHelper;
import us.mikeandwan.photos.services.PhotoApiClient;
import us.mikeandwan.photos.services.PhotoStorage;
import us.mikeandwan.photos.services.poller.MawPollerService;
import us.mikeandwan.photos.services.poller.MawScheduleReceiver;


@Singleton
@Component(modules = {ApplicationModule.class, DataStorageModule.class, PhotoApiModule.class, PreferenceModule.class})
public interface ApplicationComponent {
    // identify which services will be available to dependent components
    Application application();
    CategoryDisplayPreference categoryDisplayPreference();
    NotificationPreference notificationPreference();
    PhotoDisplayPreference photoDisplayPreference();
    SyncPreference syncPreference();
    MawDataManager mawDataManager();
    PhotoStorage photoStorage();
    PhotoApiClient photoApiClient();
    AuthenticationExceptionHandler authenticationExceptionHandler();


    void inject(MawApplication application);

    // activities
    void inject(BaseActivity activity);
    void inject(SettingsActivity activity);

    // services
    void inject(MawDataManager dataManager);
    void inject(MawSQLiteOpenHelper sqliteOpenHelper);
    void inject(PhotoApiClient client);
    void inject(PhotoStorage photoStorage);
    void inject(MawPollerService service);
    void inject(MawScheduleReceiver scheduleReceiver);
    void inject(AuthenticationExceptionHandler authenticationExceptionHandler);
}

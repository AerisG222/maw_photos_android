package us.mikeandwan.photos.di;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Component;
import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.models.KeyStore;
import us.mikeandwan.photos.prefs.CategoryDisplayPreference;
import us.mikeandwan.photos.prefs.NotificationPreference;
import us.mikeandwan.photos.prefs.PhotoDisplayPreference;
import us.mikeandwan.photos.prefs.SyncPreference;
import us.mikeandwan.photos.services.AuthenticationExceptionHandler;
import us.mikeandwan.photos.services.DataServices;
import us.mikeandwan.photos.services.DatabaseAccessor;
import us.mikeandwan.photos.services.EncryptionService;
import us.mikeandwan.photos.services.MawSQLiteOpenHelper;
import us.mikeandwan.photos.services.PhotoApiClient;
import us.mikeandwan.photos.services.PhotoStorage;
import us.mikeandwan.photos.services.poller.MawPollerService;
import us.mikeandwan.photos.services.poller.MawScheduleReceiver;
import us.mikeandwan.photos.ui.BaseActivity;
import us.mikeandwan.photos.ui.settings.SettingsActivity;


@Singleton
@Component(modules = {ApplicationModule.class, DataStorageModule.class, PhotoApiModule.class, PreferenceModule.class, EncryptionModule.class})
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
    KeyStore keyStore();
    EncryptionService encryptionServices();


    void inject(MawApplication application);

    // activities
    void inject(BaseActivity activity);
    void inject(SettingsActivity activity);

    // services
    void inject(DatabaseAccessor databaseAccessor);
    void inject(MawSQLiteOpenHelper sqliteOpenHelper);
    void inject(PhotoApiClient client);
    void inject(PhotoStorage photoStorage);
    void inject(MawPollerService service);
    void inject(MawScheduleReceiver scheduleReceiver);
    void inject(AuthenticationExceptionHandler authenticationExceptionHandler);
    void inject(KeyStore keystore);
    void inject(EncryptionService encryptionService);
}

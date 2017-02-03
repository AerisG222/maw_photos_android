package us.mikeandwan.photos.di;

import android.app.Application;
import android.content.SharedPreferences;

import javax.inject.Singleton;

import dagger.Component;
import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.activities.BaseActivity;
import us.mikeandwan.photos.activities.SettingsActivity;
import us.mikeandwan.photos.services.AuthenticationExceptionHandler;
import us.mikeandwan.photos.services.MawDataManager;
import us.mikeandwan.photos.services.MawSQLiteOpenHelper;
import us.mikeandwan.photos.services.PhotoApiClient;
import us.mikeandwan.photos.services.PhotoStorage;
import us.mikeandwan.photos.services.poller.MawPollerService;
import us.mikeandwan.photos.services.poller.MawScheduleReceiver;


@Singleton
@Component(modules = {ApplicationModule.class, DataStorageModule.class, PhotoApiModule.class})
public interface ApplicationComponent {
    // identify which services will be available to dependent components
    Application application();
    SharedPreferences sharedPreferences();
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

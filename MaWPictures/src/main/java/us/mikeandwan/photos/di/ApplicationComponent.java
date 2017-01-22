package us.mikeandwan.photos.di;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Component;
import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.activities.BaseActivity;
import us.mikeandwan.photos.fragments.MainImageFragment;
import us.mikeandwan.photos.services.MawDataManager;
import us.mikeandwan.photos.services.MawSQLiteOpenHelper;
import us.mikeandwan.photos.services.PhotoApiClient;
import us.mikeandwan.photos.services.PhotoStorage;


@Singleton
@Component(modules = {ApplicationModule.class, DataStorageModule.class, PhotoApiModule.class})
public interface ApplicationComponent {
    // identify which services will be available to dependent components
    Application application();
    MawDataManager mawDataManager();
    PhotoStorage photoStorage();
    PhotoApiClient photoApiClient();

    void inject(MawApplication application);

    // activities
    void inject(BaseActivity activity);

    // fragments
    void inject(MainImageFragment fragment);

    // services
    void inject(MawDataManager dataManager);
    void inject(MawSQLiteOpenHelper sqliteOpenHelper);
    void inject(PhotoApiClient client);
    void inject(PhotoStorage photoStorage);
}

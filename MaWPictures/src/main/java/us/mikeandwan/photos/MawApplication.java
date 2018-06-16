package us.mikeandwan.photos;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

import us.mikeandwan.photos.di.ApplicationComponent;
import us.mikeandwan.photos.di.ApplicationModule;
import us.mikeandwan.photos.di.DaggerApplicationComponent;
import us.mikeandwan.photos.di.DataStorageModule;
import us.mikeandwan.photos.di.PhotoApiModule;
import us.mikeandwan.photos.di.PreferenceModule;


public class MawApplication extends Application {
    public static final String LOG_TAG = "maw";
    public static final int JOB_ID_UPDATE_CATEGORY = 2;

    private int _notificationCount = 0;
    private ApplicationComponent _applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        _applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .dataStorageModule(new DataStorageModule())
                .photoApiModule(new PhotoApiModule())
                .preferenceModule(new PreferenceModule())
                .build();

        _applicationComponent.dataServices().wipeTempFiles();
    }


    public int getNotificationCount() {
        return _notificationCount;
    }


    public void setNotificationCount(int count) {
        _notificationCount = count;
    }


    public ApplicationComponent getApplicationComponent() {
        return _applicationComponent;
    }
}

package us.mikeandwan.photos;

import android.app.Application;

import us.mikeandwan.photos.di.ApplicationModule;
import us.mikeandwan.photos.di.DaggerApplicationComponent;
import us.mikeandwan.photos.di.DataStorageModule;
import us.mikeandwan.photos.di.ApplicationComponent;
import us.mikeandwan.photos.di.PhotoApiModule;


// TODO: provide wrapper for settings
public class MawApplication extends Application {
    public static final String LOG_TAG = "maw";
    private int _notificationCount = 0;
    private ApplicationComponent _applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        _applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .dataStorageModule(new DataStorageModule())
                .photoApiModule(new PhotoApiModule())
                .build();
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

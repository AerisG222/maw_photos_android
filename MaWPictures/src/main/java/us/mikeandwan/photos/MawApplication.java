package us.mikeandwan.photos;

import android.app.Application;


public class MawApplication extends Application {
    public static final String LOG_TAG = "maw";
    private static int _notificationCount = 0;


    public static int getNotificationCount() {
        return _notificationCount;
    }


    public static void setNotificationCount(int count) {
        _notificationCount = count;
    }
}

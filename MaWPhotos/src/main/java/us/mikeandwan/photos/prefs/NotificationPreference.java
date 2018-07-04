package us.mikeandwan.photos.prefs;

import android.content.SharedPreferences;


public class NotificationPreference {
    private final SharedPreferences _sharedPrefs;


    public NotificationPreference(SharedPreferences sharedPrefs) {
        _sharedPrefs = sharedPrefs;
    }


    public boolean getDoNotify() {
        return _sharedPrefs.getBoolean("notifications_new_message", true);
    }


    public String getNotificationRingtone() {
        return _sharedPrefs.getString("notifications_new_message_ringtone", "");
    }


    public boolean getDoVibrate() {
        return _sharedPrefs.getBoolean("notifications_new_message_vibrate", false);
    }
}

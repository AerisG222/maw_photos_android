package us.mikeandwan.photos.preferences

import android.content.SharedPreferences

class NotificationPreference(private val _sharedPrefs: SharedPreferences) {
    val doNotify: Boolean
        get() = _sharedPrefs.getBoolean("notifications_new_message", true)
    val doVibrate: Boolean
        get() = _sharedPrefs.getBoolean("notifications_new_message_vibrate", false)
}
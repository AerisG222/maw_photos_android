package us.mikeandwan.photos.prefs

import android.content.SharedPreferences

class NotificationPreference(private val _sharedPrefs: SharedPreferences) {
    val doNotify: Boolean
        get() = _sharedPrefs.getBoolean("notifications_new_message", true)
    val notificationRingtone: String
        get() = _sharedPrefs.getString("notifications_new_message_ringtone", "")!!
    val doVibrate: Boolean
        get() = _sharedPrefs.getBoolean("notifications_new_message_vibrate", false)
}
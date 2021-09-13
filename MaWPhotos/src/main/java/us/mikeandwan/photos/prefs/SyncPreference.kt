package us.mikeandwan.photos.prefs

import android.content.SharedPreferences

class SyncPreference(private val _sharedPrefs: SharedPreferences) {
    val syncFrequencyInHours: Int
        get() {
            val value = _sharedPrefs.getString("sync_frequency", "24")
            return value!!.toInt()
        }
}
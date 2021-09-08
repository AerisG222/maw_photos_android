package us.mikeandwan.photos.prefs

import android.content.SharedPreferences
import us.mikeandwan.photos.prefs.CategoryDisplay

class SyncPreference(private val _sharedPrefs: SharedPreferences) {
    val syncFrequencyInHours: Int
        get() {
            val `val` = _sharedPrefs.getString("sync_frequency", "24")
            return `val`!!.toInt()
        }
}
package us.mikeandwan.photos.preferences

import android.content.SharedPreferences

class PhotoDisplayPreference(private val _sharedPrefs: SharedPreferences) {
    val doDisplayPhotoToolbar: Boolean
        get() = _sharedPrefs.getBoolean("display_toolbar", true)
    val doDisplayThumbnails: Boolean
        get() = _sharedPrefs.getBoolean("display_thumbnails", true)
    val doDisplayTopToolbar: Boolean
        get() = _sharedPrefs.getBoolean("display_top_toolbar", true)
    val doFadeControls: Boolean
        get() = _sharedPrefs.getBoolean("fade_controls", true)
    val slideshowIntervalInSeconds: Int
        get() = _sharedPrefs.getString("slideshow_interval", "3")!!.toInt()
}
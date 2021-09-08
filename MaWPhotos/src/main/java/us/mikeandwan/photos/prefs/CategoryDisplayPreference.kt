package us.mikeandwan.photos.prefs

import android.content.SharedPreferences
import us.mikeandwan.photos.prefs.CategoryDisplay

class CategoryDisplayPreference(private val _sharedPrefs: SharedPreferences) {
    val categoryDisplay: CategoryDisplay
        get() {
            val `val` = _sharedPrefs.getString("category_view_mode", "list")
            return if (`val` == null || `val`.equals("list", ignoreCase = true)) {
                CategoryDisplay.ThumbnailAndNameList
            } else CategoryDisplay.ThumbnailGrid
        }
}
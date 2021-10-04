package us.mikeandwan.photos.prefs

import android.content.SharedPreferences

class CategoryDisplayPreference(private val _sharedPrefs: SharedPreferences) {
    val categoryDisplay: CategoryDisplay
        get() {
            val value = _sharedPrefs.getString("category_view_mode", "list")
            return if (value == null || value.equals("list", ignoreCase = true)) {
                CategoryDisplay.List
            } else CategoryDisplay.Grid
        }
}
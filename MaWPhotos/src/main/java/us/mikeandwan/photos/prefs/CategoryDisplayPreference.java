package us.mikeandwan.photos.prefs;

import android.content.SharedPreferences;


public class CategoryDisplayPreference {
    private final SharedPreferences _sharedPrefs;


    public CategoryDisplayPreference(SharedPreferences sharedPrefs) {
        _sharedPrefs = sharedPrefs;
    }


    public CategoryDisplay getCategoryDisplay() {
        String val = _sharedPrefs.getString("category_view_mode", "list");

        if(val.equalsIgnoreCase("list")) {
            return CategoryDisplay.ThumbnailAndNameList;
        }

        return CategoryDisplay.ThumbnailGrid;
    }
}

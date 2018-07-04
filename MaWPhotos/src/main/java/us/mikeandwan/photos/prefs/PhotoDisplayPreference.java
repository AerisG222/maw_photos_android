package us.mikeandwan.photos.prefs;

import android.content.SharedPreferences;


public class PhotoDisplayPreference {
    private final SharedPreferences _sharedPrefs;


    public PhotoDisplayPreference(SharedPreferences sharedPrefs) {
        _sharedPrefs = sharedPrefs;
    }


    public boolean getDoDisplayPhotoToolbar() {
        return _sharedPrefs.getBoolean("display_toolbar", true);
    }


    public boolean getDoDisplayThumbnails() {
        return _sharedPrefs.getBoolean("display_thumbnails", true);
    }


    public boolean getDoDisplayTopToolbar() {
        return _sharedPrefs.getBoolean("display_top_toolbar", true);
    }


    public boolean getDoFadeControls() {
        return _sharedPrefs.getBoolean("fade_controls", true);
    }


    public int getSlideshowIntervalInSeconds() {
        return Integer.parseInt(_sharedPrefs.getString("slideshow_interval", "3"));
    }
}

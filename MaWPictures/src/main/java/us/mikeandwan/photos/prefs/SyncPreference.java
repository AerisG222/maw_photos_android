package us.mikeandwan.photos.prefs;


import android.content.SharedPreferences;

public class SyncPreference {
    private final SharedPreferences _sharedPrefs;


    public SyncPreference(SharedPreferences sharedPrefs) {
        _sharedPrefs = sharedPrefs;
    }


    public int getSyncFrequencyInHours() {
        String val = _sharedPrefs.getString("sync_frequency", "24");

        return Integer.parseInt(val);
    }
}

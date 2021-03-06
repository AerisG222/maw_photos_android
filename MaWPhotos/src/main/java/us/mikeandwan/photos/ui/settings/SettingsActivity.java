package us.mikeandwan.photos.ui.settings;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;

import javax.inject.Inject;

import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.R;
import us.mikeandwan.photos.services.UpdateCategoriesJobScheduler;


public class SettingsActivity extends PreferenceActivity {
    @Inject SharedPreferences _sharedPrefs;

    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }


    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
            & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((MawApplication) getApplication()).getApplicationComponent().inject(this);

        getFragmentManager().beginTransaction().replace(android.R.id.content, new PhotosPreferenceFragment()).commit();
    }


    private static final Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = (preference, value) -> {
        String stringValue = value.toString();

        if (preference.getKey().equals("sync_frequency")) {
            if (!stringValue.equals(preference.getSharedPreferences().getString("sync_frequency", "24"))) {
                UpdateCategoriesJobScheduler updateScheduler = MawApplication.getInstance().getApplicationComponent().updateCategoriesJobScheduler();

                long millis = Integer.parseInt(stringValue) * 60 * 60 * 1000;

                updateScheduler.schedule(false, millis);
            }
        }

        if (preference instanceof RingtonePreference) {
            // For ringtone preferences, look up the correct display value
            // using RingtoneManager.
            if (TextUtils.isEmpty(stringValue)) {
                // Empty values correspond to 'silent' (no ringtone).
                preference.setSummary(R.string.pref_notifications_silent);
            } else {
                Ringtone ringtone = RingtoneManager.getRingtone(preference.getContext(), Uri.parse(stringValue));

                if (ringtone == null) {
                    preference.setSummary(null);
                } else {
                    String name = ringtone.getTitle(preference.getContext());
                    preference.setSummary(name);
                }
            }
        }

        return true;
    };


    private static void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
            PreferenceManager
                .getDefaultSharedPreferences(preference.getContext())
                .getString(preference.getKey(), ""));
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class PhotosPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            bindPreferenceSummaryToValue(findPreference("slideshow_interval"));
            bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
        }
    }
}

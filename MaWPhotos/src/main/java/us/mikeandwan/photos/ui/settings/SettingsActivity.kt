package us.mikeandwan.photos.ui.settings

import android.content.Context
import android.content.res.Configuration
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.preference.Preference
import android.preference.Preference.OnPreferenceChangeListener
import android.preference.PreferenceActivity
import android.preference.PreferenceManager
import android.preference.RingtonePreference
import android.text.TextUtils
import us.mikeandwan.photos.R

//TODO: fixme
//@AndroidEntryPoint
class SettingsActivity : PreferenceActivity() {
    //@Inject
    //lateinit var _updateScheduler: UpdateCategoriesJobScheduler

    override fun onIsMultiPane(): Boolean {
        return isXLargeTablet(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //getFragmentManager().beginTransaction().replace(android.R.id.content, new PhotosPreferenceFragment()).commit();
    }

    private val sBindPreferenceSummaryToValueListener =
        OnPreferenceChangeListener { preference: Preference, value: Any ->
            val stringValue = value.toString()
            if (preference.key == "sync_frequency") {
                if (stringValue != preference.sharedPreferences.getString("sync_frequency", "24")) {
                    val millis = (stringValue.toInt() * 60 * 60 * 1000).toLong()
                    //_updateScheduler!!.schedule(false, millis)
                }
            }
            if (preference is RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary(R.string.pref_notifications_silent)
                } else {
                    val ringtone =
                        RingtoneManager.getRingtone(preference.getContext(), Uri.parse(stringValue))
                    if (ringtone == null) {
                        preference.setSummary(null)
                    } else {
                        val name = ringtone.getTitle(preference.getContext())
                        preference.setSummary(name)
                    }
                }
            }
            true
        }

    private fun bindPreferenceSummaryToValue(preference: Preference) {
        preference.onPreferenceChangeListener = sBindPreferenceSummaryToValueListener
        sBindPreferenceSummaryToValueListener.onPreferenceChange(
            preference,
            PreferenceManager
                .getDefaultSharedPreferences(preference.context)
                .getString(preference.key, "")
        )
    } /*
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class PhotosPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            bindPreferenceSummaryToValue(findPreference("slideshow_interval"));
            bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
        }
    }*/

    companion object {
        private fun isXLargeTablet(context: Context): Boolean {
            return (context.resources.configuration.screenLayout
                    and Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE
        }
    }
}
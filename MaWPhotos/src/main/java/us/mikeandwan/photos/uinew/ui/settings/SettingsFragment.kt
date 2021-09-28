package us.mikeandwan.photos.uinew.ui.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import us.mikeandwan.photos.R

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
}
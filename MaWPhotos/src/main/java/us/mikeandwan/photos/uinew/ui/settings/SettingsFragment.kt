package us.mikeandwan.photos.uinew.ui.settings

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.preference.PreferenceFragmentCompat
import dagger.hilt.android.AndroidEntryPoint
import us.mikeandwan.photos.R

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {
    private val viewModel by viewModels<SettingsViewModel>()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.preferenceDataStore = viewModel.dataStore

        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
}
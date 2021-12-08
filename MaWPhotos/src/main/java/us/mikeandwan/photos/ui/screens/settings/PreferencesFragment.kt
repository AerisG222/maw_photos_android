package us.mikeandwan.photos.ui.screens.settings

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.preference.PreferenceFragmentCompat
import dagger.hilt.android.AndroidEntryPoint
import us.mikeandwan.photos.R

@AndroidEntryPoint
class PreferencesFragment : PreferenceFragmentCompat() {
    private val viewModel by viewModels<PreferencesViewModel>()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.preferenceDataStore = viewModel.dataStore

        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
}
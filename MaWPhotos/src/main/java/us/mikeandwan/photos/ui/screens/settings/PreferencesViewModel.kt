package us.mikeandwan.photos.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PreferencesViewModel @Inject constructor (
    val dataStore: PreferenceDataStore
): ViewModel()
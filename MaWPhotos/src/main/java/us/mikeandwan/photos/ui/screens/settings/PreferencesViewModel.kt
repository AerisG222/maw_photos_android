package us.mikeandwan.photos.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import us.mikeandwan.photos.domain.ErrorRepository
import us.mikeandwan.photos.domain.NotificationPreferenceRepository
import javax.inject.Inject

@HiltViewModel
class PreferencesViewModel @Inject constructor (
    val dataStore: PreferenceDataStore,
    val repo: NotificationPreferenceRepository,
    val errorRepository: ErrorRepository
): ViewModel()
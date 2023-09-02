package us.mikeandwan.photos.ui.screens.settings

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.preference.PreferenceFragmentCompat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import us.mikeandwan.photos.R
import us.mikeandwan.photos.domain.MawPreferenceDataStore

@AndroidEntryPoint
class PreferencesFragment : PreferenceFragmentCompat() {
    private val viewModel by viewModels<PreferencesViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initStateObservers()

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.preferenceDataStore = viewModel.dataStore

        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    private fun initStateObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                setDatabaseShowNotificationFlag(getDatabaseShowNotificationFlag() && areNotificationsPermitted(), false)

                viewModel.repo.getDoNotify()
                    .distinctUntilChanged()
                    .mapLatest { v -> handleShowNotificationChange(v) }
                    .launchIn(this)
            }
        }
    }

    private fun handleShowNotificationChange(doShow: Boolean) {
        // tiramisu added permissions for notifications, so only worry about this if we are
        // on a newer device
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if(doShow && !areNotificationsPermitted()) {
                requestPermissionLauncher.launch("android.permission.POST_NOTIFICATIONS")
            }
        }
    }

    private fun areNotificationsPermitted(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return  ContextCompat.checkSelfPermission(
                requireContext(),
                "android.permission.POST_NOTIFICATIONS"
            ) == PackageManager.PERMISSION_GRANTED
        }

        return true
    }

    private fun getDatabaseShowNotificationFlag(): Boolean {
        return (viewModel.dataStore as MawPreferenceDataStore).getShowNotifications()
    }

    private fun setDatabaseShowNotificationFlag(doShow: Boolean, refreshUi: Boolean) {
        (viewModel.dataStore as MawPreferenceDataStore).setShowNotifications(doShow)

        // if the user denies the permission, we need to refresh the preferences screen
        // otherwise it will show as enabled
        if(!doShow && refreshUi) {
            viewModel.errorRepository.showError("Please enable the Notification permission under Settings > Apps > Maw Photos")

            onCreate(null)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean -> setDatabaseShowNotificationFlag(isGranted, true) }
}
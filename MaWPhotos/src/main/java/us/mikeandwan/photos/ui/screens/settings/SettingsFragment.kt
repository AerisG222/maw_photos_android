package us.mikeandwan.photos.ui.screens.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import us.mikeandwan.photos.ui.theme.AppTheme

@AndroidEntryPoint
class SettingsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme {
                    SettingsScreen()
                }
            }
        }
    }

    /*
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
     */
}
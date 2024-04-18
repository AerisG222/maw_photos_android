package us.mikeandwan.photos.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import coil.compose.AsyncImage
import us.mikeandwan.photos.R
import us.mikeandwan.photos.domain.models.CategoryDisplayType
import us.mikeandwan.photos.domain.models.GridThumbnailSize
import us.mikeandwan.photos.domain.models.NavigationArea

const val SettingsRoute = "settings"

fun NavGraphBuilder.settingsScreen(
    onNavigateToLogin: () -> Unit,
    updateTopBar : (Boolean, Boolean, String) -> Unit,
    setNavArea: (NavigationArea) -> Unit
) {
    composable(SettingsRoute) {
        val viewModel: SettingsViewModel = hiltViewModel()

        val notificationDoNotify by viewModel.notificationDoNotify.collectAsStateWithLifecycle()
        val notificationDoVibrate by viewModel.notificationDoVibrate.collectAsStateWithLifecycle()

        val categoryDisplayType by viewModel.categoryDisplayType.collectAsStateWithLifecycle()
        val categoryThumbnailSize by viewModel.categoryThumbnailSize.collectAsStateWithLifecycle()

        val photoSlideshowInterval by viewModel.photoSlideshowInterval.collectAsStateWithLifecycle()
        val photoThumbnailSize by viewModel.photoThumbnailSize.collectAsStateWithLifecycle()

        val randomSlideshowInterval by viewModel.randomSlideshowInterval.collectAsStateWithLifecycle()
        val randomThumbnailSize by viewModel.randomThumbnailSize.collectAsStateWithLifecycle()

        val searchQueryCount by viewModel.searchQueryCount.collectAsStateWithLifecycle()
        val searchDisplayType by viewModel.searchDisplayType.collectAsStateWithLifecycle()
        val searchThumbnailSize by viewModel.searchThumbnailSize.collectAsStateWithLifecycle()

        updateTopBar(true, false, "Settings")
        setNavArea(NavigationArea.Settings)

        SettingsScreen(
            notificationDoNotify,
            notificationDoVibrate,
            categoryDisplayType,
            categoryThumbnailSize,
            photoSlideshowInterval,
            photoThumbnailSize,
            randomSlideshowInterval,
            randomThumbnailSize,
            searchQueryCount,
            searchDisplayType,
            searchThumbnailSize,
            setNotificationDoNotify = { viewModel.setNotificationDoNotify(it) },
            setNotificationDoVibrate = { viewModel.setNotificationDoVibrate(it) },
            setCategoryDisplayType = { viewModel.setCategoryDisplayType(it) },
            setCategoryThumbnailSize = { viewModel.setCategoryThumbnailSize(it) },
            setPhotoSlideshowInterval = { viewModel.setPhotoSlideshowInterval(it) },
            setPhotoThumbnailSize = { viewModel.setPhotoThumbnailSize(it) },
            setRandomSlideshowInterval = { viewModel.setRandomSlideshowInterval(it) },
            setRandomThumbnailSize = { viewModel.setRandomThumbnailSize(it) },
            setSearchQueryCount = { viewModel.setSearchQueryCount(it) },
            setSearchDisplayType = { viewModel.setSearchDisplayType(it) },
            setSearchThumbnailSize = { viewModel.setSearchThumbnailSize(it) },
            logout = {
                viewModel.logout()
                onNavigateToLogin()
            }
        )
    }
}

fun NavController.navigateToSettings() {
    this.navigate(SettingsRoute)
}

@Composable
fun SettingsScreen(
    notificationDoNotify: Boolean,
    notificationDoVibrate: Boolean,
    categoryDisplayType: CategoryDisplayType,
    categoryThumbnailSize: GridThumbnailSize,
    photoSlideshowInterval: Int,
    photoThumbnailSize: GridThumbnailSize,
    randomSlideshowInterval: Int,
    randomThumbnailSize: GridThumbnailSize,
    searchQueryCount: Int,
    searchDisplayType: CategoryDisplayType,
    searchThumbnailSize: GridThumbnailSize,
    setNotificationDoNotify: (Boolean) -> Unit,
    setNotificationDoVibrate: (Boolean) -> Unit,
    setCategoryDisplayType: (CategoryDisplayType) -> Unit,
    setCategoryThumbnailSize: (GridThumbnailSize) -> Unit,
    setPhotoSlideshowInterval: (Int) -> Unit,
    setPhotoThumbnailSize: (GridThumbnailSize) -> Unit,
    setRandomSlideshowInterval: (Int) -> Unit,
    setRandomThumbnailSize: (GridThumbnailSize) -> Unit,
    setSearchQueryCount: (Int) -> Unit,
    setSearchDisplayType: (CategoryDisplayType) -> Unit,
    setSearchThumbnailSize: (GridThumbnailSize) -> Unit,
    logout: () -> Unit
) {
    val displayTypeList = listOf("Grid", "List")
    val thumbnailSizeList = listOf("ExtraSmall", "Small", "Medium", "Large")
    val slideshowIntervalList = listOf("1s", "2s", "3s", "4s", "5s", "10s", "15s", "20s", "30s")

    var categoryDisplayTypeMenuExpanded by remember { mutableStateOf(false) }
    var categoryGridItemSizeMenuExpanded by remember { mutableStateOf(false) }
    var photoSlideshowMenuExpanded by remember { mutableStateOf(false) }
    var photoGridItemSizeMenuExpanded by remember { mutableStateOf(false) }
    var randomSlideshowMenuExpanded by remember { mutableStateOf(false) }
    var randomGridItemSizeMenuExpanded by remember { mutableStateOf(false) }
    var searchQueryCountMenuExpanded by remember { mutableStateOf(false) }
    var searchDisplayTypeMenuExpanded by remember { mutableStateOf(false) }
    var searchGridItemSizeMenuExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        // --- NOTIFICATIONS ----
        Heading(stringId = R.string.pref_notifications_header)
        SwitchPreference(
            labelStringId = R.string.pref_notifications_new_message_title,
            isChecked = notificationDoNotify,
            onChange = { setNotificationDoNotify(it) }
        )
        SwitchPreference(
            labelStringId = R.string.pref_notifications_vibrate,
            isChecked = notificationDoVibrate,
            onChange = { setNotificationDoVibrate(it) }
        )

        // --- CATEGORY LIST ----
        Heading(stringId = R.string.pref_category_display_header)
        MenuPreference(
            expanded = categoryDisplayTypeMenuExpanded,
            labelStringId = R.string.pref_category_display_header,
            options = displayTypeList,
            selectedValue = categoryDisplayType.toString(),
            onRequestOpen = { categoryDisplayTypeMenuExpanded = true },
            onSelect = {
                categoryDisplayTypeMenuExpanded = false
                setCategoryDisplayType(enumValueOf(it))
            }
        )
        MenuPreference(
            expanded = categoryGridItemSizeMenuExpanded,
            labelStringId = R.string.grid_thumbnail_size,
            options = thumbnailSizeList,
            selectedValue = categoryThumbnailSize.toString(),
            onRequestOpen = { categoryGridItemSizeMenuExpanded = true },
            onSelect = {
                categoryGridItemSizeMenuExpanded = false
                setCategoryThumbnailSize(enumValueOf(it))
            }
        )

        // --- CATEGORY / PHOTO ----
        Heading(stringId = R.string.pref_photo_display_header)
        MenuPreference(
            expanded = photoSlideshowMenuExpanded,
            labelStringId = R.string.pref_photo_display_slideshow_interval,
            options = slideshowIntervalList,
            selectedValue = "${photoSlideshowInterval}s",
            onRequestOpen = { photoSlideshowMenuExpanded = true },
            onSelect = {
                photoSlideshowMenuExpanded = false
                setPhotoSlideshowInterval(it.substring(0, -1).toInt())
            }
        )
        MenuPreference(
            expanded = photoGridItemSizeMenuExpanded,
            labelStringId = R.string.grid_thumbnail_size,
            options = thumbnailSizeList,
            selectedValue = photoThumbnailSize.toString(),
            onRequestOpen = { photoGridItemSizeMenuExpanded = true },
            onSelect = {
                photoGridItemSizeMenuExpanded = false
                setPhotoThumbnailSize(enumValueOf(it))
            }
        )

        // --- RANDOM ----
        Heading(stringId = R.string.pref_random_display_header)
        MenuPreference(
            expanded = randomSlideshowMenuExpanded,
            labelStringId = R.string.pref_photo_display_slideshow_interval,
            options = slideshowIntervalList,
            selectedValue = "${randomSlideshowInterval}s",
            onRequestOpen = { randomSlideshowMenuExpanded = true },
            onSelect = {
                randomSlideshowMenuExpanded = false
                setRandomSlideshowInterval(it.substring(0, -1).toInt())
            }
        )
        MenuPreference(
            expanded = randomGridItemSizeMenuExpanded,
            labelStringId = R.string.grid_thumbnail_size,
            options = thumbnailSizeList,
            selectedValue = randomThumbnailSize.toString(),
            onRequestOpen = { randomGridItemSizeMenuExpanded = true },
            onSelect = {
                randomGridItemSizeMenuExpanded = false
                setRandomThumbnailSize(enumValueOf(it))
            }
        )

        // --- SEARCH ----
        Heading(stringId = R.string.pref_search_display_header)
        MenuPreference(
            expanded = searchQueryCountMenuExpanded,
            labelStringId = R.string.pref_search_query_count_to_remember,
            options = listOf("5", "10", "20", "30", "50"),
            selectedValue = searchQueryCount.toString(),
            onRequestOpen = { searchQueryCountMenuExpanded = true },
            onSelect = {
                searchQueryCountMenuExpanded = false
                setSearchQueryCount(it.toInt())
            }
        )
        MenuPreference(
            expanded = searchDisplayTypeMenuExpanded,
            labelStringId = R.string.pref_category_display_header,
            options = displayTypeList,
            selectedValue = searchDisplayType.toString(),
            onRequestOpen = { searchDisplayTypeMenuExpanded = true },
            onSelect = {
                searchDisplayTypeMenuExpanded = false
                setSearchDisplayType(enumValueOf(it))
            }
        )
        MenuPreference(
            expanded = searchGridItemSizeMenuExpanded,
            labelStringId = R.string.grid_thumbnail_size,
            options = thumbnailSizeList,
            selectedValue = searchThumbnailSize.toString(),
            onRequestOpen = { searchGridItemSizeMenuExpanded = true },
            onSelect = {
                searchGridItemSizeMenuExpanded = false
                setSearchThumbnailSize(enumValueOf(it))
            }
        )

        // --- ADVANCED ----
        Heading(stringId = R.string.pref_advanced_display_header)
        Button(onClick = logout) {
            AsyncImage(
                model = R.drawable.ic_logout,
                contentDescription = stringResource(id = R.string.fragment_settings_log_out),
                modifier = Modifier.padding(8.dp)
            )

            Text(text = stringResource(id = R.string.fragment_settings_log_out))
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
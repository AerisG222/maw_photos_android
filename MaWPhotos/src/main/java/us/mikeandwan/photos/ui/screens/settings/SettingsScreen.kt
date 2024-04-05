package us.mikeandwan.photos.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import us.mikeandwan.photos.R
import us.mikeandwan.photos.domain.models.CategoryDisplayType
import us.mikeandwan.photos.domain.models.GridThumbnailSize

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel()
) {
    val coroutineScope = rememberCoroutineScope()

    val notificationDoNotify = viewModel.notificationPreferenceRepository.getDoNotify().collectAsState(initial = false)
    val notificationDoVibrate = viewModel.notificationPreferenceRepository.getDoVibrate().collectAsState(initial = true)

    val categoryDisplayType = viewModel.categoryPreferenceRepository.getCategoryDisplayType().collectAsState(initial = CategoryDisplayType.Grid )
    val categoryThumbSize = viewModel.categoryPreferenceRepository.getCategoryGridItemSize().collectAsState(initial = GridThumbnailSize.Medium)

    val photoSlideshowInterval = viewModel.photoPreferenceRepository.getSlideshowIntervalSeconds().collectAsState(initial = 3 )
    val photoThumbSize = viewModel.photoPreferenceRepository.getPhotoGridItemSize().collectAsState(initial = GridThumbnailSize.Medium)

    val randomSlideshowInterval = viewModel.randomPreferenceRepository.getSlideshowIntervalSeconds().collectAsState(initial = 3 )
    val randomThumbSize = viewModel.randomPreferenceRepository.getPhotoGridItemSize().collectAsState(initial = GridThumbnailSize.Medium)

    val searchQueryCount = viewModel.searchPreferenceRepository.getSearchesToSaveCount().collectAsState(initial = 20)
    val searchDisplayType = viewModel.searchPreferenceRepository.getSearchDisplayType().collectAsState(initial = CategoryDisplayType.Grid)
    val searchThumbSize = viewModel.searchPreferenceRepository.getSearchGridItemSize().collectAsState(initial = GridThumbnailSize.Medium)

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
            isChecked = notificationDoNotify.value,
            onChange = { doNotify ->
                coroutineScope.launch {
                    viewModel.notificationPreferenceRepository.setDoNotify(doNotify)
                }
            }
        )
        SwitchPreference(
            labelStringId = R.string.pref_notifications_vibrate,
            isChecked = notificationDoVibrate.value,
            onChange = { doVibrate ->
                coroutineScope.launch {
                    viewModel.notificationPreferenceRepository.setDoVibrate(doVibrate)
                }
            }
        )

        // --- CATEGORY LIST ----
        Heading(stringId = R.string.pref_category_display_header)
        MenuPreference(
            expanded = categoryDisplayTypeMenuExpanded,
            labelStringId = R.string.pref_category_display_header,
            options = displayTypeList,
            selectedValue = categoryDisplayType.value.toString(),
            onRequestOpen = { categoryDisplayTypeMenuExpanded = true },
            onSelect = {
                categoryDisplayTypeMenuExpanded = false

                coroutineScope.launch {
                    viewModel.categoryPreferenceRepository.setCategoryDisplayType(enumValueOf(it))
                }
            }
        )
        MenuPreference(
            expanded = categoryGridItemSizeMenuExpanded,
            labelStringId = R.string.grid_thumbnail_size,
            options = thumbnailSizeList,
            selectedValue = categoryThumbSize.value.toString(),
            onRequestOpen = { categoryGridItemSizeMenuExpanded = true },
            onSelect = {
                categoryGridItemSizeMenuExpanded = false

                coroutineScope.launch {
                    viewModel.categoryPreferenceRepository.setCategoryGridItemSize(enumValueOf(it))
                }
            }
        )

        // --- CATEGORY / PHOTO ----
        Heading(stringId = R.string.pref_photo_display_header)
        MenuPreference(
            expanded = photoSlideshowMenuExpanded,
            labelStringId = R.string.pref_photo_display_slideshow_interval,
            options = slideshowIntervalList,
            selectedValue = "${photoSlideshowInterval.value}s",
            onRequestOpen = { photoSlideshowMenuExpanded = true },
            onSelect = {
                photoSlideshowMenuExpanded = false

                coroutineScope.launch {
                    viewModel.photoPreferenceRepository.setSlideshowIntervalSeconds(it.substring(0, -1).toInt())
                }
            }
        )
        MenuPreference(
            expanded = photoGridItemSizeMenuExpanded,
            labelStringId = R.string.grid_thumbnail_size,
            options = thumbnailSizeList,
            selectedValue = photoThumbSize.value.toString(),
            onRequestOpen = { photoGridItemSizeMenuExpanded = true },
            onSelect = {
                photoGridItemSizeMenuExpanded = false

                coroutineScope.launch {
                    viewModel.photoPreferenceRepository.setPhotoGridItemSize(enumValueOf(it))
                }
            }
        )

        // --- RANDOM ----
        Heading(stringId = R.string.pref_random_display_header)
        MenuPreference(
            expanded = randomSlideshowMenuExpanded,
            labelStringId = R.string.pref_photo_display_slideshow_interval,
            options = slideshowIntervalList,
            selectedValue = "${randomSlideshowInterval.value}s",
            onRequestOpen = { randomSlideshowMenuExpanded = true },
            onSelect = {
                randomSlideshowMenuExpanded = false

                coroutineScope.launch {
                    viewModel.randomPreferenceRepository.setSlideshowIntervalSeconds(it.substring(0, -1).toInt())
                }
            }
        )
        MenuPreference(
            expanded = randomGridItemSizeMenuExpanded,
            labelStringId = R.string.grid_thumbnail_size,
            options = thumbnailSizeList,
            selectedValue = randomThumbSize.value.toString(),
            onRequestOpen = { randomGridItemSizeMenuExpanded = true },
            onSelect = {
                randomGridItemSizeMenuExpanded = false

                coroutineScope.launch {
                    viewModel.randomPreferenceRepository.setPhotoGridItemSize(enumValueOf(it))
                }
            }
        )

        // --- SEARCH ----
        Heading(stringId = R.string.pref_search_display_header)
        MenuPreference(
            expanded = searchQueryCountMenuExpanded,
            labelStringId = R.string.pref_search_query_count_to_remember,
            options = listOf("5", "10", "20", "30", "50"),
            selectedValue = searchQueryCount.value.toString(),
            onRequestOpen = { searchQueryCountMenuExpanded = true },
            onSelect = {
                searchQueryCountMenuExpanded = false

                coroutineScope.launch {
                    viewModel.searchPreferenceRepository.setSearchesToSaveCount(it.toInt())
                }
            }
        )
        MenuPreference(
            expanded = searchDisplayTypeMenuExpanded,
            labelStringId = R.string.pref_category_display_header,
            options = displayTypeList,
            selectedValue = searchDisplayType.value.toString(),
            onRequestOpen = { searchDisplayTypeMenuExpanded = true },
            onSelect = {
                searchDisplayTypeMenuExpanded = false

                coroutineScope.launch {
                    viewModel.searchPreferenceRepository.setSearchDisplayType(enumValueOf(it))
                }
            }
        )
        MenuPreference(
            expanded = searchGridItemSizeMenuExpanded,
            labelStringId = R.string.grid_thumbnail_size,
            options = thumbnailSizeList,
            selectedValue = searchThumbSize.value.toString(),
            onRequestOpen = { searchGridItemSizeMenuExpanded = true },
            onSelect = {
                searchGridItemSizeMenuExpanded = false

                coroutineScope.launch {
                    viewModel.searchPreferenceRepository.setSearchGridItemSize(enumValueOf(it))
                }
            }
        )

        // --- ADVANCED ----
        Heading(stringId = R.string.pref_advanced_display_header)
        Button(
            onClick = { }
        ) {
            AsyncImage(
                model = R.drawable.ic_logout,
                contentDescription = stringResource(id = R.string.fragment_settings_log_out),
                modifier = Modifier.padding(8.dp)
            )

            Text(text = stringResource(id = R.string.fragment_settings_log_out))
        }
    }
}
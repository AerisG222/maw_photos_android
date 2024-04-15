package us.mikeandwan.photos.ui.controls.navigationrail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import us.mikeandwan.photos.R
import us.mikeandwan.photos.domain.models.NavigationArea
import us.mikeandwan.photos.ui.controls.yearnavmenu.YearListMenu

@Composable
fun NavigationRail(
    activeArea: NavigationArea,
    navigateToCategories: () -> Unit,
    navigateToCategoriesByYear: (Int) -> Unit,
    navigateToSearch: () -> Unit,
    navigateToRandom: () -> Unit,
    navigateToUpload: () -> Unit,
    navigateToAbout: () -> Unit,
    navigateToSettings: () -> Unit
) {
    @Composable
    fun GetColor(area: NavigationArea, activeArea: NavigationArea): Color {
        return if(area == activeArea) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurface
        }
    }

    Row(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier
            .fillMaxHeight()
            .width(64.dp)
        ) {
            AsyncImage(
                model = R.drawable.ic_home,
                contentDescription = stringResource(R.string.categories_icon_description),
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(
                    GetColor(NavigationArea.Category, activeArea),
                    blendMode = BlendMode.Modulate
                ),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 8.dp, bottom = 8.dp)
                    .width(32.dp)
                    .clickable { navigateToCategories() }
            )
            AsyncImage(
                model = R.drawable.ic_search,
                contentDescription = stringResource(R.string.search_icon_description),
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(
                    GetColor(NavigationArea.Search, activeArea),
                    blendMode = BlendMode.Modulate
                ),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 8.dp, bottom = 8.dp)
                    .width(32.dp)
                    .clickable { navigateToSearch() }
            )
            AsyncImage(
                model = R.drawable.ic_shuffle,
                contentDescription = stringResource(R.string.random_photos_icon_description),
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(
                    GetColor(NavigationArea.Random, activeArea),
                    blendMode = BlendMode.Modulate
                ),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 8.dp, bottom = 8.dp)
                    .width(32.dp)
                    .clickable { navigateToRandom() }
            )

            Spacer(modifier = Modifier.weight(1f))

            AsyncImage(
                model = R.drawable.ic_file_upload,
                contentDescription = stringResource(R.string.upload_queue_icon_description),
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(
                    GetColor(NavigationArea.Upload, activeArea),
                    blendMode = BlendMode.Modulate
                ),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 8.dp, bottom = 8.dp)
                    .width(32.dp)
                    .clickable { navigateToUpload() }
            )
            AsyncImage(
                model = R.drawable.ic_help_outline,
                contentDescription = stringResource(R.string.help_icon_description),
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(
                    GetColor(NavigationArea.About, activeArea),
                    blendMode = BlendMode.Modulate
                ),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 8.dp, bottom = 8.dp)
                    .width(32.dp)
                    .clickable { navigateToAbout() }
            )
            AsyncImage(
                model = R.drawable.ic_settings,
                contentDescription = stringResource(R.string.settings_icon_description),
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(
                    GetColor(NavigationArea.Settings, activeArea),
                    blendMode = BlendMode.Modulate
                ),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 8.dp, bottom = 8.dp)
                    .width(32.dp)
                    .clickable { navigateToSettings() }
            )
        }

        Column(modifier = Modifier.fillMaxSize()) {
            YearListMenu(onYearSelected = { year -> navigateToCategoriesByYear(year) })
        }
    }
}

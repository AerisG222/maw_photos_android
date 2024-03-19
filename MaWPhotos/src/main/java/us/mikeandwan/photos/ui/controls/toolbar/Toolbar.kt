package us.mikeandwan.photos.ui.controls.toolbar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import us.mikeandwan.photos.R

@Composable
fun Toolbar(
    viewModel: ToolbarViewModel = viewModel()
) {
    val showAppIcon = viewModel.showAppIcon.collectAsState()
    val showSearch = viewModel.showSearch.collectAsState()
    val toolbarTitle = viewModel.toolbarTitle.collectAsState()
    val searchTerm = viewModel.searchTerm.collectAsState()

    Row(modifier = Modifier
        .fillMaxWidth()
    ) {
        if(showAppIcon.value) {
            AsyncImage(
                model = R.drawable.ic_launch,
                contentDescription = stringResource(R.string.application_menu_icon_description),
                alignment = Alignment.Center,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .height(56.dp)
                    .width(56.dp)
                    .padding(8.dp)
                    .clickable {
                        viewModel.onAppIconClicked()
                    }
            )
        } else {
            AsyncImage(
                model = R.drawable.ic_arrow_back,
                contentDescription = stringResource(R.string.navigate_back_icon_description),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary, blendMode = BlendMode.Modulate),
                alignment = Alignment.Center,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .height(56.dp)
                    .width(56.dp)
                    .padding(8.dp)
                    .clickable {
                        viewModel.onBackClicked()
                    }
            )
        }

        if(showSearch.value) {
            TextField(
                value = searchTerm.value,
                singleLine = true,
                onValueChange = {
                    viewModel.setSearchTerm(it)
                },
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(12.dp, 0.dp)
            )

            AsyncImage(
                model = R.drawable.ic_search,
                contentDescription = stringResource(R.string.search_icon_description),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary, blendMode = BlendMode.Modulate),
                alignment = Alignment.Center,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .height(56.dp)
                    .width(56.dp)
                    .padding(0.dp, 0.dp, 16.dp, 0.dp)
                    .clickable {
                        viewModel.search()
                    }
            )
        } else {
            Text(
                text = toolbarTitle.value,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        }
    }
}

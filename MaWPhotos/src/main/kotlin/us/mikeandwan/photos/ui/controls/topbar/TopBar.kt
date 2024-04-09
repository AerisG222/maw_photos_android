package us.mikeandwan.photos.ui.controls.topbar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import us.mikeandwan.photos.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    onExpandNavMenu: () -> Unit,
    viewModel: TopBarViewModel = hiltViewModel()
) {
    val showAppIcon = viewModel.showAppIcon.collectAsState()
    val toolbarTitle = viewModel.toolbarTitle.collectAsState()
    val showSearch = viewModel.showSearch.collectAsState()
    val searchTerm = viewModel.searchTerm.collectAsState()

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Text(
                text = toolbarTitle.value,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            if(showAppIcon.value) {
                AsyncImage(
                    model = R.drawable.ic_launch,
                    contentDescription = stringResource(R.string.application_menu_icon_description),
                    alignment = Alignment.Center,
                    modifier = Modifier
                        //.align(Alignment.CenterVertically)
                        .height(56.dp)
                        .width(56.dp)
                        .padding(8.dp)
                        .clickable { onExpandNavMenu() }
                )
            } else {
                AsyncImage(
                    model = R.drawable.ic_arrow_back,
                    contentDescription = stringResource(R.string.navigate_back_icon_description),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary, blendMode = BlendMode.Modulate),
                    alignment = Alignment.Center,
                    modifier = Modifier
                        //.align(Alignment.CenterVertically)
                        .height(56.dp)
                        .width(56.dp)
                        .padding(8.dp)
                        .clickable {
                            viewModel.onBackClicked()
                        }
                )
            }
        },
//                    actions = {
//                        IconButton(onClick = { /* do something */ }) {
//                            Icon(
//                                imageVector = Icons.Filled.Menu,
//                                contentDescription = "Localized description"
//                            )
//                        }
//                    },
        //scrollBehavior = scrollBehavior,
    )

//    Row(modifier = Modifier
//        .fillMaxWidth()
//    ) {
//        if(showSearch.value) {
//            TextField(
//                value = searchTerm.value,
//                singleLine = true,
//                onValueChange = {
//                    viewModel.setSearchTerm(it)
//                },
//                modifier = Modifier
//                    .align(Alignment.CenterVertically)
//                    .padding(12.dp, 0.dp)
//            )
//
//            AsyncImage(
//                model = R.drawable.ic_search,
//                contentDescription = stringResource(R.string.search_icon_description),
//                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary, blendMode = BlendMode.Modulate),
//                alignment = Alignment.Center,
//                modifier = Modifier
//                    .align(Alignment.CenterVertically)
//                    .height(56.dp)
//                    .width(56.dp)
//                    .padding(0.dp, 0.dp, 16.dp, 0.dp)
//                    .clickable {
//                        viewModel.search()
//                    }
//            )
//        } else {
//            Text(
//                text = toolbarTitle.value,
//                style = MaterialTheme.typography.headlineMedium,
//                modifier = Modifier
//                    .align(Alignment.CenterVertically)
//                    .fillMaxWidth()
//                    .padding(8.dp)
//            )
//        }
//    }
}

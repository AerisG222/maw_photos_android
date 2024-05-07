package us.mikeandwan.photos.ui.screens.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import us.mikeandwan.photos.R
import us.mikeandwan.photos.domain.models.CategoryDisplayType
import us.mikeandwan.photos.domain.models.GridThumbnailSize
import us.mikeandwan.photos.domain.models.NavigationArea
import us.mikeandwan.photos.domain.models.PhotoCategory
import us.mikeandwan.photos.ui.controls.categorylist.CategoryList
import us.mikeandwan.photos.ui.controls.imagegrid.ImageGrid
import us.mikeandwan.photos.ui.toImageGridItem

const val SearchRoute = "search"
private const val searchTermArg = "searchTermArg"

fun NavGraphBuilder.searchScreen(
    onNavigateToCategory: (PhotoCategory) -> Unit,
    updateTopBar : (Boolean, Boolean, String) -> Unit,
    setNavArea: (NavigationArea) -> Unit
) {
    composable(
        route = "$SearchRoute?term={$searchTermArg}",
        arguments = listOf(
            navArgument(searchTermArg) {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            }
        )
    ) { backStackEntry ->
        val vm: SearchViewModel = hiltViewModel()
        val searchTerm = backStackEntry.arguments?.getString(searchTermArg)

        LaunchedEffect(searchTerm) {
            if(searchTerm != null) {
                vm.search(searchTerm)
            }
        }

        val results by vm.searchResultsAsCategories.collectAsStateWithLifecycle()
        val totalFound by vm.totalFound.collectAsStateWithLifecycle()
        val displayType by vm.displayType.collectAsStateWithLifecycle()
        val thumbSize by vm.gridItemThumbnailSize.collectAsStateWithLifecycle()

        updateTopBar(true, true, "Search")
        setNavArea(NavigationArea.Search)

        SearchScreen(
            results,
            totalFound,
            displayType,
            thumbSize,
            onNavigateToCategory = onNavigateToCategory,
            continueSearch = { vm.continueSearch() }
        )
    }
}

fun NavController.navigateToSearch() {
    this.navigate(SearchRoute)
}

fun NavController.navigateToSearch(searchTerm: String) {
    navigate("$SearchRoute?term=$searchTerm")
}

@Composable
fun SearchScreen(
    results: List<PhotoCategory>,
    totalFound: Int,
    displayType: CategoryDisplayType,
    thumbSize: GridThumbnailSize,
    onNavigateToCategory: (PhotoCategory) -> Unit,
    continueSearch: () -> Unit
) {
    if(results.isEmpty()) {
        AsyncImage(
            model = R.drawable.ic_search,
            contentDescription = stringResource(id = R.string.search_icon_description),
            modifier = Modifier
                .padding(40.dp)
                .fillMaxSize()
        )

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.fragment_search_no_results_found),
            )
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
        ) {
            when (displayType) {
                CategoryDisplayType.Grid -> {
                    ImageGrid(
                        gridItems = results.map { it.toImageGridItem() },
                        thumbnailSize = thumbSize,
                        onSelectGridItem = { onNavigateToCategory(it.data as PhotoCategory) }
                    )
                }

                CategoryDisplayType.List -> {
                    CategoryList(
                        categories = results,
                        showYear = true,
                        onSelectCategory = onNavigateToCategory
                    )
                }

                else -> {}
            }
        }

        if (results.isNotEmpty()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if(results.size < totalFound) {
                        Button(onClick = { continueSearch() }) {
                            Text(text = stringResource(id = R.string.fragment_search_load_more))
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                    ) {
                        Text(text = results.size.toString())
                        Text(text = "/")
                        Text(text = totalFound.toString())
                    }
                }
            }
        }
    }
}

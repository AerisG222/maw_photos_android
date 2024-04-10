package us.mikeandwan.photos.ui.screens.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import us.mikeandwan.photos.domain.models.PhotoCategory
import us.mikeandwan.photos.ui.controls.categorylist.CategoryList
import us.mikeandwan.photos.ui.controls.imagegrid.ImageGrid
import us.mikeandwan.photos.ui.toImageGridItem

const val SearchRoute = "search"

fun NavGraphBuilder.searchScreen(
    onNavigateToCategory: (PhotoCategory) -> Unit
) {
    composable(SearchRoute) {
        val vm: SearchViewModel = hiltViewModel()

        val results by vm.searchResultsAsCategories.collectAsStateWithLifecycle()
        val totalFound by vm.totalFound.collectAsStateWithLifecycle()
        val displayType by vm.displayType.collectAsStateWithLifecycle()
        val thumbSize by vm.gridItemThumbnailSize.collectAsStateWithLifecycle()

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

        Text(
            text = stringResource(id = R.string.fragment_search_no_results_found)
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        when(displayType) {
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

    if(results.isNotEmpty()) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { continueSearch() }) {
                Text(text = stringResource(id = R.string.fragment_search_load_more))
            }
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            Text(text = results.size.toString())
            Text(text = "/")
            Text(text = totalFound.toString())
        }
    }
}
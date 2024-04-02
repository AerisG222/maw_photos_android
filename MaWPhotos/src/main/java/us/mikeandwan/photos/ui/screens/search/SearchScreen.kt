package us.mikeandwan.photos.ui.screens.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import us.mikeandwan.photos.R
import us.mikeandwan.photos.domain.models.CategoryDisplayType
import us.mikeandwan.photos.domain.models.PhotoCategory
import us.mikeandwan.photos.ui.controls.categorylist.CategoryList
import us.mikeandwan.photos.ui.controls.categorylist.CategoryListViewModel
import us.mikeandwan.photos.ui.controls.imagegrid.ImageGridScreen
import us.mikeandwan.photos.ui.controls.imagegrid.ImageGridViewModel
import us.mikeandwan.photos.ui.toImageGridItem

@Composable
fun SearchScreen(
    viewModel: SearchViewModel
) {
    val results = viewModel.searchResultsAsCategories.collectAsState()
    val totalFound = viewModel.totalFound.collectAsState()
    val displayType = viewModel.displayType.collectAsState()
    val thumbSize = viewModel.gridItemThumbnailSize.collectAsState()

    if(results.value.isEmpty()) {
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
        when(displayType.value) {
            CategoryDisplayType.Grid -> {
                val vm = ImageGridViewModel()
                vm.setGridItems(results.value.map { it.toImageGridItem() })
                vm.setThumbnailSize(thumbSize.value)

                ImageGridScreen(
                    viewModel = vm,
                    onSelectGridItem = {
                        viewModel.onCategoryClicked(it.data as PhotoCategory)
                    }
                )
            }

            CategoryDisplayType.List -> {
                val vm = CategoryListViewModel()
                vm.setCategories(results.value)

                CategoryList(
                    viewModel = vm,
                    onSelectListItem = viewModel.onCategoryClicked
                )
            }

            else -> {}
        }
    }

    if(results.value.isNotEmpty()) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { viewModel.continueSearch() }) {
                Text(text = stringResource(id = R.string.fragment_search_load_more))
            }
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            Text(text = results.value.size.toString())
            Text(text = "/")
            Text(text = totalFound.value.toString())
        }
    }
}
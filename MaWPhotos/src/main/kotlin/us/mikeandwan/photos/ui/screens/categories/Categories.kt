package us.mikeandwan.photos.ui.screens.categories

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import us.mikeandwan.photos.domain.models.CategoryDisplayType
import us.mikeandwan.photos.domain.models.CategoryPreference
import us.mikeandwan.photos.domain.models.NavigationArea
import us.mikeandwan.photos.domain.models.PhotoCategory
import us.mikeandwan.photos.ui.controls.categorylist.CategoryList
import us.mikeandwan.photos.ui.controls.imagegrid.ImageGrid
import us.mikeandwan.photos.ui.toImageGridItem

const val CategoriesRoute = "categories"
private const val yearArg = "year"

fun NavGraphBuilder.categoriesScreen(
    onNavigateToCategory: (PhotoCategory) -> Unit,
    updateTopBar : (Boolean, Boolean, String) -> Unit,
    setNavArea: (NavigationArea) -> Unit
) {
    composable(
        route = "$CategoriesRoute/{$yearArg}",
        arguments = listOf(
            navArgument(yearArg) { type = NavType.IntType }
        )
    ) { backStackEntry ->
        val vm: CategoriesViewModel = hiltViewModel()
        val year = backStackEntry.arguments?.getInt(yearArg) ?: 0

        LaunchedEffect(year) {
            vm.loadCategories(year)
            updateTopBar(true, true, year.toString())
            setNavArea(NavigationArea.Category)
        }

        val categories by vm.categories.collectAsStateWithLifecycle()
        val preferences by vm.preferences.collectAsStateWithLifecycle()

        CategoriesScreen(
            categories = categories,
            preferences = preferences,
            onNavigateToCategory = onNavigateToCategory,
            onRefresh = { vm.onRefreshCategories() }
        )
    }
}

fun NavController.buildCategoriesRoute(year: Int): String {
    return "${CategoriesRoute}/${year}"
}

fun NavController.navigateToCategories(year: Int) {
    navigate(buildCategoriesRoute(year)) {
        launchSingleTop = true
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    preferences: CategoryPreference,
    categories: List<PhotoCategory>,
    onNavigateToCategory: (PhotoCategory) -> Unit,
    onRefresh: suspend () -> Unit
) {
    val state = rememberPullToRefreshState()

    if(state.isRefreshing) {
        LaunchedEffect(true) {
            onRefresh()
            state.endRefresh()
        }
    }

    Box(Modifier.nestedScroll(state.nestedScrollConnection)) {
        when(preferences.displayType) {
            CategoryDisplayType.Grid -> {
                ImageGrid(
                    gridItems = categories.map{ it.toImageGridItem() },
                    thumbnailSize = preferences.gridThumbnailSize,
                    onSelectGridItem = { onNavigateToCategory(it.data as PhotoCategory) }
                )
            }
            CategoryDisplayType.List -> {
                CategoryList(
                    categories = categories,
                    showYear = false,
                    onSelectCategory = { onNavigateToCategory(it) }
                )
            }
            else -> { }
        }

        PullToRefreshContainer(
            modifier = Modifier.align(Alignment.TopCenter),
            state = state,
        )
    }
}

//                viewModel.refreshStatus
//                    .onEach {
//                        if(it.message != null) {
//                            val snackbar = Snackbar.make(binding.container.context, binding.root, it.message, Snackbar.LENGTH_SHORT)
//
//                            snackbar.show()
//                        }
//                    }
//                    .launchIn(this)

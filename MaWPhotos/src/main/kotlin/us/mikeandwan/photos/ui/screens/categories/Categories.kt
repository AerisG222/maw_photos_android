package us.mikeandwan.photos.ui.screens.categories

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import timber.log.Timber
import us.mikeandwan.photos.domain.models.CategoryDisplayType
import us.mikeandwan.photos.domain.models.NavigationArea
import us.mikeandwan.photos.domain.models.MediaCategory
import us.mikeandwan.photos.ui.controls.categorylist.CategoryList
import us.mikeandwan.photos.ui.controls.imagegrid.ImageGrid
import us.mikeandwan.photos.ui.controls.imagegrid.rememberImageGridState
import us.mikeandwan.photos.ui.controls.loading.Loading
import us.mikeandwan.photos.ui.toImageGridItem

@Serializable
data class CategoriesRoute (
    val year: Int
)

fun NavGraphBuilder.categoriesScreen(
    onNavigateToCategory: (MediaCategory) -> Unit,
    updateTopBar : (Boolean, Boolean, String) -> Unit,
    setActiveYear: (Int) -> Unit,
    setNavArea: (NavigationArea) -> Unit,
    navigateToLogin: () -> Unit,
    navigateToCategories: (Int) -> Unit
) {
    composable<CategoriesRoute> { backStackEntry ->
        val vm: CategoriesViewModel = hiltViewModel()
        val args = backStackEntry.toRoute<CategoriesRoute>()
        val state by vm.state.collectAsStateWithLifecycle()

        LaunchedEffect(Unit) {
            setNavArea(NavigationArea.Category)
        }

        LaunchedEffect(args.year) {
            vm.setYear(args.year)
            updateTopBar(true, true, args.year.toString())
            setActiveYear(args.year)
        }

        when(state) {
            is CategoriesState.Unknown -> Loading()
            is CategoriesState.NotAuthorized ->
                LaunchedEffect(Unit) {
                    navigateToLogin()
                }
            is CategoriesState.InvalidYear ->
                LaunchedEffect(Unit) {
                    navigateToCategories((state as CategoriesState.InvalidYear).mostRecentYear)
                }
            is CategoriesState.Valid ->
                CategoriesScreen(
                    state as CategoriesState.Valid,
                    onNavigateToCategory
                )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    state: CategoriesState.Valid,
    onNavigateToCategory: (MediaCategory) -> Unit,
) {
    val pullToRefreshState = rememberPullToRefreshState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.refreshStatus) {
        Timber.i("$state.refreshStatus")

        if(state.refreshStatus.message != null) {
            snackbarHostState.showSnackbar(state.refreshStatus.message)
        }
    }

    val gridState = rememberImageGridState (
        gridItems = state.categories.map { it.toImageGridItem() },
        thumbnailSize = state.preferences.gridThumbnailSize,
        onSelectGridItem = { onNavigateToCategory(it.data) }
    )

    Scaffold (
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = state.refreshStatus.isRefreshing,
            state = pullToRefreshState,
            onRefresh = { state.refreshCategories() },
            modifier = Modifier.padding(innerPadding)
        ) {
            when (state.preferences.displayType) {
                CategoryDisplayType.Grid -> {
                    ImageGrid(gridState)
                }

                CategoryDisplayType.List -> {
                    CategoryList(
                        categories = state.categories,
                        showYear = false,
                        onSelectCategory = { onNavigateToCategory(it) }
                    )
                }

                else -> {}
            }
        }
    }
}

package us.mikeandwan.photos.ui.screens.categories

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import timber.log.Timber
import us.mikeandwan.photos.domain.models.CategoryDisplayType
import us.mikeandwan.photos.domain.models.CategoryPreference
import us.mikeandwan.photos.domain.models.CategoryRefreshStatus
import us.mikeandwan.photos.domain.models.NavigationArea
import us.mikeandwan.photos.domain.models.PhotoCategory
import us.mikeandwan.photos.ui.controls.categorylist.CategoryList
import us.mikeandwan.photos.ui.controls.imagegrid.ImageGrid
import us.mikeandwan.photos.ui.controls.imagegrid.rememberImageGridState
import us.mikeandwan.photos.ui.toImageGridItem
import kotlin.random.Random

@Serializable
data class CategoriesRoute (
    val year: Int
)

fun NavGraphBuilder.categoriesScreen(
    onNavigateToCategory: (PhotoCategory) -> Unit,
    updateTopBar : (Boolean, Boolean, String) -> Unit,
    setNavArea: (NavigationArea) -> Unit
) {
    composable<CategoriesRoute> { backStackEntry ->
        val vm: CategoriesViewModel = hiltViewModel()
        val args = backStackEntry.toRoute<CategoriesRoute>()

        LaunchedEffect(args.year) {
            vm.loadCategories(args.year)
            updateTopBar(true, true, args.year.toString())
            setNavArea(NavigationArea.Category)
        }

        val categories by vm.categories.collectAsStateWithLifecycle()
        val refreshStatus by vm.refreshStatus.collectAsStateWithLifecycle()
        val preferences by vm.preferences.collectAsStateWithLifecycle()

        CategoriesScreen(
            categories = categories,
            preferences = preferences,
            refreshStatus = refreshStatus,
            onNavigateToCategory = onNavigateToCategory,
            onRefresh = { vm.onRefreshCategories(it) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    preferences: CategoryPreference,
    categories: List<PhotoCategory>,
    refreshStatus: CategoryRefreshStatus,
    onNavigateToCategory: (PhotoCategory) -> Unit,
    onRefresh: suspend (Int) -> Unit
) {
    val pullToRefreshState = rememberPullToRefreshState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(refreshStatus) {
        Timber.i("$refreshStatus")

        if(!refreshStatus.isRefreshing) {
            pullToRefreshState.endRefresh()
        }

        if(refreshStatus.message != null) {
            snackbarHostState.showSnackbar(refreshStatus.message)
        }
    }

    if(pullToRefreshState.isRefreshing) {
        LaunchedEffect(Unit) {
            onRefresh(Random.nextInt())
        }
    }

    val gridState = rememberImageGridState(
        gridItems = categories.map { it.toImageGridItem() },
        thumbnailSize = preferences.gridThumbnailSize,
        onSelectGridItem = { onNavigateToCategory(it.data as PhotoCategory) }
    )

    Scaffold (
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
        Box(Modifier
            .padding(innerPadding)
            .nestedScroll(pullToRefreshState.nestedScrollConnection)
        ) {
            when (preferences.displayType) {
                CategoryDisplayType.Grid -> {
                    ImageGrid(gridState)
                }

                CategoryDisplayType.List -> {
                    CategoryList(
                        categories = categories,
                        showYear = false,
                        onSelectCategory = { onNavigateToCategory(it) }
                    )
                }

                else -> {}
            }

            PullToRefreshContainer(
                modifier = Modifier.align(Alignment.TopCenter),
                state = pullToRefreshState,
            )
        }
    }
}

package us.mikeandwan.photos.ui.screens.category

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import us.mikeandwan.photos.domain.models.Media
import us.mikeandwan.photos.domain.models.NavigationArea
import us.mikeandwan.photos.ui.controls.imagegrid.ImageGrid
import us.mikeandwan.photos.ui.controls.imagegrid.rememberImageGridState
import us.mikeandwan.photos.ui.controls.loading.Loading
import us.mikeandwan.photos.ui.controls.topbar.TopBarState

@Serializable
data class CategoryRoute (
    val mediaType: String,
    val categoryId: Int
)

fun NavGraphBuilder.categoryScreen(
    updateTopBar : (TopBarState) -> Unit,
    setNavArea: (NavigationArea) -> Unit,
    navigateToMedia: (Media) -> Unit,
    navigateToLogin: () -> Unit
) {
    composable<CategoryRoute> { backStackEntry ->
        val vm: CategoryViewModel = hiltViewModel()
        val args = backStackEntry.toRoute<CategoryRoute>()
        val state by vm.state.collectAsStateWithLifecycle()

        LaunchedEffect(args.mediaType, args.categoryId) {
            vm.initState(args.mediaType, args.categoryId)
        }

        when(state) {
            is CategoryState.NotAuthorized -> {
                LaunchedEffect(state) {
                    navigateToLogin()
                }
            }
            is CategoryState.Loading -> Loading()
            is CategoryState.CategoryLoaded -> {
                val s = state as CategoryState.CategoryLoaded

                LaunchedEffect(s.category) {
                    updateTopBar(
                        TopBarState().copy(
                            title = s.category.name
                        )
                    )
                }
            }
            is CategoryState.Loaded -> {
                val s = state as CategoryState.Loaded

                LaunchedEffect(s.category) {
                    updateTopBar(
                        TopBarState().copy(
                            title = s.category.name
                        )
                    )
                }

                CategoryScreen(
                    s,
                    setNavArea,
                    navigateToMedia
                )
            }
            is CategoryState.Error -> { }  // rely on error snackbar message
        }
    }
}

@Composable
fun CategoryScreen(
    state: CategoryState.Loaded,
    setNavArea: (NavigationArea) -> Unit,
    navigateToMedia: (Media) -> Unit
) {
    LaunchedEffect(Unit) {
        setNavArea(NavigationArea.Category)
    }

    val gridState = rememberImageGridState(
        gridItems = state.gridItems,
        thumbnailSize = state.gridItemThumbnailSize,
        onSelectGridItem = { navigateToMedia(it.data) }
    )

    ImageGrid(gridState)
}

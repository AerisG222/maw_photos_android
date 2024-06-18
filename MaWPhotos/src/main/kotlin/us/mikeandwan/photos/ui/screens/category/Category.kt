package us.mikeandwan.photos.ui.screens.category

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import us.mikeandwan.photos.domain.models.Media
import us.mikeandwan.photos.domain.models.MediaType
import us.mikeandwan.photos.domain.models.NavigationArea
import us.mikeandwan.photos.ui.controls.imagegrid.ImageGrid
import us.mikeandwan.photos.ui.controls.loading.Loading

@Serializable
data class CategoryRoute (
    val mediaType: String,
    val categoryId: Int
)

fun NavGraphBuilder.categoryScreen(
    updateTopBar : (Boolean, Boolean, String) -> Unit,
    setNavArea: (NavigationArea) -> Unit,
    navigateToMedia: (Media) -> Unit
) {
    composable<CategoryRoute> { backStackEntry ->
        val vm: CategoryViewModel = hiltViewModel()
        val args = backStackEntry.toRoute<CategoryRoute>()
        val state = rememberCategoryState(vm, MediaType.valueOf(args.mediaType), args.categoryId, navigateToMedia)

        LaunchedEffect(Unit) {
            setNavArea(NavigationArea.Category)
        }

        when(state) {
            is CategoryState.Loading -> {
                Loading()
            }
            is CategoryState.CategoryLoaded -> {
                LaunchedEffect(state.category) {
                    updateTopBar(true, true, state.category.name)
                }

                Loading()
            }
            is CategoryState.Loaded -> {
                LaunchedEffect(state.category) {
                    updateTopBar(true, true, state.category.name)
                }

                CategoryScreen(state)
            }
        }
    }
}

@Composable
fun CategoryScreen(state: CategoryState.Loaded) {
    ImageGrid(state.gridState)
}

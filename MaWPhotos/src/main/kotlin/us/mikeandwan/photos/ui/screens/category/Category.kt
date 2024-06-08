package us.mikeandwan.photos.ui.screens.category

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import us.mikeandwan.photos.domain.models.NavigationArea
import us.mikeandwan.photos.ui.controls.imagegrid.ImageGrid
import us.mikeandwan.photos.ui.controls.loading.Loading

@Serializable
data class CategoryRoute (
    val categoryId: Int
)

fun NavGraphBuilder.categoryScreen(
    updateTopBar : (Boolean, Boolean, String) -> Unit,
    setNavArea: (NavigationArea) -> Unit,
    navigateToCategoryPhoto: (categoryId: Int, photoId: Int) -> Unit
) {
    composable<CategoryRoute> { backStackEntry ->
        val vm: CategoryViewModel = hiltViewModel()
        val args = backStackEntry.toRoute<CategoryRoute>()
        val state = rememberCategoryState(vm, args.categoryId, navigateToCategoryPhoto)

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

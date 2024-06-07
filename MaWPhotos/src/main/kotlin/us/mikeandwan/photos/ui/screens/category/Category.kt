package us.mikeandwan.photos.ui.screens.category

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import us.mikeandwan.photos.domain.models.NavigationArea
import us.mikeandwan.photos.ui.controls.imagegrid.ImageGrid
import us.mikeandwan.photos.ui.controls.loading.Loading

const val CategoryRoute = "category"
const val categoryIdArg = "categoryId"

fun NavGraphBuilder.categoryScreen(
    updateTopBar : (Boolean, Boolean, String) -> Unit,
    setNavArea: (NavigationArea) -> Unit,
    navigateToCategoryPhoto: (categoryId: Int, photoId: Int) -> Unit
) {
    composable(
        route = "$CategoryRoute/{$categoryIdArg}",
        arguments = listOf(
            navArgument(categoryIdArg) { type = NavType.IntType }
        )
    ) { backStackEntry ->
        val vm: CategoryViewModel = hiltViewModel()
        val categoryId = backStackEntry.arguments?.getInt(categoryIdArg) ?: -1
        val state = rememberCategoryState(vm, categoryId, navigateToCategoryPhoto)

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

fun NavController.navigateToCategory(categoryId: Int) {
    this.navigate("$CategoryRoute/$categoryId")
}

@Composable
fun CategoryScreen(state: CategoryState.Loaded) {
    ImageGrid(state.gridState)
}

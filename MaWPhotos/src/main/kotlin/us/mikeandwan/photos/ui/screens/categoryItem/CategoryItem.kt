package us.mikeandwan.photos.ui.screens.categoryItem

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import us.mikeandwan.photos.domain.models.NavigationArea
import us.mikeandwan.photos.ui.controls.loading.Loading
import us.mikeandwan.photos.ui.controls.photopager.PhotoPager
import us.mikeandwan.photos.ui.screens.category.CategoryRoute
import us.mikeandwan.photos.ui.screens.category.categoryIdArg

private const val photoIdArg = "photoId"

fun NavGraphBuilder.categoryItemScreen(
    updateTopBar : (Boolean, Boolean, String) -> Unit,
    setNavArea: (NavigationArea) -> Unit,
) {
    composable(
        route = "$CategoryRoute/{$categoryIdArg}/{$photoIdArg}",
        arguments = listOf(
            navArgument(categoryIdArg) { type = NavType.IntType },
            navArgument(photoIdArg) { type = NavType.IntType }
        )
    ) { backStackEntry ->
        val vm: CategoryItemViewModel = hiltViewModel()
        val categoryId = backStackEntry.arguments?.getInt(categoryIdArg) ?: -1
        val photoId = backStackEntry.arguments?.getInt(photoIdArg) ?: -1
        val state = rememberCategoryItemState(vm, categoryId, photoId)

        LaunchedEffect(Unit) {
            setNavArea(NavigationArea.Category)
        }

        when(state) {
            is CategoryItemState.Loading -> {
                Loading()
            }
            is CategoryItemState.CategoryLoaded -> {
                LaunchedEffect(state.category) {
                    updateTopBar(true, true, state.category.name)
                }

                Loading()
            }
            is CategoryItemState.Loaded -> {
                LaunchedEffect(state.category) {
                    updateTopBar(true, true, state.category.name)
                }

                CategoryItemScreen(state)
            }
        }
    }
}

fun NavController.navigateToCategoryPhoto(categoryId: Int, photoId: Int) {
    this.navigate("$CategoryRoute/$categoryId/$photoId")
}

@Composable
fun CategoryItemScreen(
    state: CategoryItemState.Loaded
) {
    PhotoPager(
        state.photoPagerState,
        navigateToYear = { },
        navigateToCategory = { }
    )
}

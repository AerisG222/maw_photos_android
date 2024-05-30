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
import us.mikeandwan.photos.ui.controls.photopager.PhotoPager

const val CategoryRoute = "category"
private const val categoryIdArg = "categoryId"
private const val photoIdArg = "photoId"

fun NavGraphBuilder.categoryScreen(
    updateTopBar : (Boolean, Boolean, String) -> Unit,
    setNavArea: (NavigationArea) -> Unit,
    navigateToPhoto: (categoryId: Int, photoId: Int) -> Unit
) {
    composable(
        route = "$CategoryRoute/{$categoryIdArg}?photoId={$photoIdArg}",
        arguments = listOf(
            navArgument(categoryIdArg) { type = NavType.IntType },
            navArgument(photoIdArg) { type = NavType.IntType; defaultValue = -1 }
        )
    ) { backStackEntry ->
        val vm: CategoryViewModel = hiltViewModel()
        val categoryId = backStackEntry.arguments?.getInt(categoryIdArg) ?: -1
        val photoId = backStackEntry.arguments?.getInt(photoIdArg) ?: -1
        val state = rememberCategoryState(vm, categoryId, photoId)

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

                CategoryScreen(
                    state,
                    navigateToPhoto = navigateToPhoto
                )
            }
        }
    }
}

fun NavController.navigateToCategory(categoryId: Int, photoId: Int? = null) {
    if(photoId == null) {
        this.navigate("$CategoryRoute/$categoryId")
    } else {
        this.navigate("$CategoryRoute/$categoryId?$photoIdArg=$photoId")
    }
}

@Composable
fun CategoryScreen(
    state: CategoryState.Loaded,
    navigateToPhoto: (categoryId: Int, photoId: Int) -> Unit
) {
    if(state.showImageGrid) {
        ImageGrid(state.gridState)
    } else {
        PhotoPager(
            state.photoPagerState,
            navigateToYear = { },
            navigateToCategory = { }
        )
    }
}

package us.mikeandwan.photos.ui.screens.categoryItem

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import us.mikeandwan.photos.domain.models.NavigationArea
import us.mikeandwan.photos.ui.controls.loading.Loading
import us.mikeandwan.photos.ui.controls.photopager.PhotoPager

@Serializable
data class CategoryItemRoute (
    val categoryId: Int,
    val photoId: Int
)

fun NavGraphBuilder.categoryItemScreen(
    updateTopBar : (Boolean, Boolean, String) -> Unit,
    setNavArea: (NavigationArea) -> Unit,
) {
    composable<CategoryItemRoute> { backStackEntry ->
        val vm: CategoryItemViewModel = hiltViewModel()
        val args = backStackEntry.toRoute<CategoryItemRoute>()
        val state = rememberCategoryItemState(vm, args.categoryId, args.photoId)

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

package us.mikeandwan.photos.ui.screens.randomItem

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import us.mikeandwan.photos.domain.models.NavigationArea
import us.mikeandwan.photos.domain.models.PhotoCategory
import us.mikeandwan.photos.ui.controls.loading.Loading
import us.mikeandwan.photos.ui.controls.photopager.PhotoPager

@Serializable
data class RandomItemRoute (
    val photoId: Int
)

fun NavGraphBuilder.randomItemScreen(
    updateTopBar : (Boolean, Boolean, String) -> Unit,
    setNavArea: (NavigationArea) -> Unit,
    navigateToYear: (Int) -> Unit,
    navigateToCategory: (PhotoCategory) -> Unit
) {
    composable<RandomItemRoute> { backStackEntry ->
        val vm: RandomItemViewModel = hiltViewModel()
        val args = backStackEntry.toRoute<RandomItemRoute>()
        val state = rememberRandomItemState(vm, args.photoId)

        LaunchedEffect(Unit) {
            updateTopBar(true, true, "Random")
            setNavArea(NavigationArea.Random)
        }

        DisposableEffect(Unit) {
            vm.onResume()

            onDispose {
                vm.onPause()
            }
        }

        when(state) {
            is RandomItemState.Loading -> {
                Loading()
            }
            is RandomItemState.Loaded -> {
                RandomItemScreen(
                    state,
                    navigateToYear,
                    navigateToCategory
                )
            }
        }
    }
}

@Composable
fun RandomItemScreen(
    state: RandomItemState.Loaded,
    navigateToYear: (Int) -> Unit,
    navigateToCategory: (PhotoCategory) -> Unit
) {
    PhotoPager(
        state.pagerState,
        navigateToYear = navigateToYear,
        navigateToCategory = navigateToCategory
    )
}

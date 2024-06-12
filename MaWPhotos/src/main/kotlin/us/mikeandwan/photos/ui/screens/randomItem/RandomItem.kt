package us.mikeandwan.photos.ui.screens.randomItem

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import us.mikeandwan.photos.domain.models.NavigationArea
import us.mikeandwan.photos.domain.models.PhotoCategory
import us.mikeandwan.photos.ui.controls.loading.Loading
import us.mikeandwan.photos.ui.controls.metadata.DetailBottomSheet
import us.mikeandwan.photos.ui.controls.photopager.ButtonBar
import us.mikeandwan.photos.ui.controls.photopager.OverlayPositionCount
import us.mikeandwan.photos.ui.controls.photopager.OverlayYearName
import us.mikeandwan.photos.ui.controls.photopager.PhotoPager
import us.mikeandwan.photos.ui.controls.photopager.rememberRotation
import us.mikeandwan.photos.ui.controls.scaffolds.ItemPagerScaffold

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RandomItemScreen(
    state: RandomItemState.Loaded,
    navigateToYear: (Int) -> Unit,
    navigateToCategory: (PhotoCategory) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    val rotationState = rememberRotation(state.activePhotoIndex)

    ItemPagerScaffold(
        showDetails = state.showDetails,
        topLeftContent = {
            OverlayYearName(
                category = state.category,
                onClickYear = { year -> navigateToYear(year) },
                onClickCategory = { category -> navigateToCategory(category) })
        },
        topRightContent = {
            OverlayPositionCount(
                position = state.activePhotoIndex + 1,
                count = state.photos.size
            )
        },
        bottomBarContent = {
            ButtonBar(
                isSlideshowPlaying = state.isSlideshowPlaying,
                onRotateLeft = { rotationState.setActiveRotation(-90f) },
                onRotateRight = { rotationState.setActiveRotation(90f) },
                onToggleSlideshow = state.toggleSlideshow,
                onShare = {
                    coroutineScope.launch {
                       // sharePhoto(context, state.savePhotoToShare, state.photos[state.activePhotoIndex])
                    }
                },
                onViewDetails = state.toggleDetails
            )
        },
        detailSheetContent = {
            DetailBottomSheet(
                activePhotoId = state.activePhotoId,
                sheetState = sheetState,
                ratingState = state.ratingState,
                exifState = state.exifState,
                commentState = state.commentState,
                onDismissRequest = state.toggleDetails
            )
        }
    ) {
        PhotoPager(
            state.photos,
            state.activePhotoIndex,
            rotationState.activeRotation,
            setActiveIndex = { index -> state.setActiveIndex(index) }
        )
    }
}

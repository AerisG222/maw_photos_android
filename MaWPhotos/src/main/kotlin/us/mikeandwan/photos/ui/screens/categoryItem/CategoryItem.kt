package us.mikeandwan.photos.ui.screens.categoryItem

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import us.mikeandwan.photos.ui.controls.scaffolds.ItemPagerScaffold
import us.mikeandwan.photos.ui.controls.loading.Loading
import us.mikeandwan.photos.ui.controls.metadata.DetailBottomSheet
import us.mikeandwan.photos.ui.controls.photopager.ButtonBar
import us.mikeandwan.photos.ui.controls.photopager.OverlayPositionCount
import us.mikeandwan.photos.ui.controls.photopager.PhotoPager
import us.mikeandwan.photos.ui.controls.photopager.rememberRotation
import us.mikeandwan.photos.ui.sharePhoto

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryItemScreen(
    state: CategoryItemState.Loaded
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    val rotationState = rememberRotation(state.activePhotoIndex)

    ItemPagerScaffold(
        showDetails = state.showDetails,
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
                        sharePhoto(context, state.savePhotoToShare, state.activePhoto)
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

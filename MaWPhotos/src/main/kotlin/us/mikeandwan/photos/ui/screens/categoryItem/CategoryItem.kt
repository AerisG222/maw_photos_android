package us.mikeandwan.photos.ui.screens.categoryItem

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import us.mikeandwan.photos.domain.models.NavigationArea
import us.mikeandwan.photos.domain.models.Photo
import us.mikeandwan.photos.ui.controls.scaffolds.ItemPagerScaffold
import us.mikeandwan.photos.ui.controls.loading.Loading
import us.mikeandwan.photos.ui.controls.metadata.DetailBottomSheet
import us.mikeandwan.photos.ui.controls.photopager.ButtonBar
import us.mikeandwan.photos.ui.controls.photopager.OverlayPositionCount
import us.mikeandwan.photos.ui.controls.photopager.PhotoPager
import us.mikeandwan.photos.ui.controls.photopager.rememberRotation
import us.mikeandwan.photos.utils.getFilenameFromUrl
import java.io.File

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
                        sharePhoto(context, state.savePhotoToShare, state.photos[state.activePhotoIndex])
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

private suspend fun sharePhoto(
    ctx: Context,
    savePhotoToShare: (drawable: Drawable, filename: String, onComplete: (File) -> Unit) -> Unit,
    photo: Photo
) {
    val drawable = getPhotoToShare(ctx, photo)

    savePhotoToShare(
        drawable,
        getFilenameFromUrl(photo.mdUrl)
    ) { fileToShare ->
        val contentUri = FileProvider.getUriForFile(ctx, "us.mikeandwan.photos.fileprovider", fileToShare)
        val sendIntent = Intent(Intent.ACTION_SEND)

        sendIntent.setDataAndType(contentUri, "image/*")
        sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        sendIntent.putExtra(Intent.EXTRA_STREAM, contentUri)

        val shareIntent = Intent.createChooser(sendIntent, null)

        ctx.startActivity(shareIntent)
    }
}

private suspend fun getPhotoToShare(ctx: Context, photo: Photo): Drawable {
    return withContext(Dispatchers.IO) {
        val loader = ImageLoader(ctx)
        val request = ImageRequest.Builder(ctx)
            .data(photo.mdUrl)
            .allowHardware(false) // Disable hardware bitmaps.
            .build()

        (loader.execute(request) as SuccessResult).drawable
    }
}

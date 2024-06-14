package us.mikeandwan.photos.ui.screens.randomItem

import android.graphics.drawable.Drawable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import us.mikeandwan.photos.domain.models.NavigationArea
import us.mikeandwan.photos.domain.models.Photo
import us.mikeandwan.photos.domain.models.PhotoCategory
import us.mikeandwan.photos.ui.controls.loading.Loading
import us.mikeandwan.photos.ui.controls.metadata.CommentState
import us.mikeandwan.photos.ui.controls.metadata.DetailBottomSheet
import us.mikeandwan.photos.ui.controls.metadata.ExifState
import us.mikeandwan.photos.ui.controls.metadata.RatingState
import us.mikeandwan.photos.ui.controls.metadata.rememberCommentState
import us.mikeandwan.photos.ui.controls.metadata.rememberExifState
import us.mikeandwan.photos.ui.controls.metadata.rememberRatingState
import us.mikeandwan.photos.ui.controls.photopager.ButtonBar
import us.mikeandwan.photos.ui.controls.photopager.OverlayPositionCount
import us.mikeandwan.photos.ui.controls.photopager.OverlayYearName
import us.mikeandwan.photos.ui.controls.photopager.PhotoPager
import us.mikeandwan.photos.ui.controls.photopager.rememberRotation
import us.mikeandwan.photos.ui.sharePhoto
import us.mikeandwan.photos.ui.controls.scaffolds.ItemPagerScaffold
import java.io.File

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

        val category by vm.category.collectAsStateWithLifecycle()
        val photos by vm.photos.collectAsStateWithLifecycle()
        val activePhotoId by vm.activeId.collectAsStateWithLifecycle()
        val activePhotoIndex by vm.activeIndex.collectAsStateWithLifecycle()
        val activePhoto by vm.activePhoto.collectAsStateWithLifecycle()
        val isSlideshowPlaying by vm.isSlideshowPlaying.collectAsStateWithLifecycle()
        val showDetailSheet by vm.showDetailSheet.collectAsStateWithLifecycle()

        LaunchedEffect(photos, args.photoId) {
            if(photos.isNotEmpty() && args.photoId > 0) {
                vm.setActiveId(args.photoId)
            }
        }

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

        // rating
        val userRating by vm.userRating.collectAsStateWithLifecycle()
        val averageRating by vm.averageRating.collectAsStateWithLifecycle()
        val ratingState = rememberRatingState(
            userRating = userRating,
            averageRating = averageRating,
            fetchRating = { vm.fetchRatingDetails() },
            updateUserRating = { vm.setRating(it) }
        )

        // exif
        val exif by vm.exif.collectAsStateWithLifecycle()
        val exifState = rememberExifState(
            exif,
            fetchExif = { vm.fetchExif() }
        )

        // comments
        val comments by vm.comments.collectAsStateWithLifecycle()
        val commentState = rememberCommentState(
            comments = comments,
            fetchComments = { vm.fetchCommentDetails() },
            addComment = { vm.addComment(it) }
        )

        if(category == null || activePhoto == null) {
            Loading()
        } else {
            RandomItemScreen(
                category!!,
                photos,
                activePhotoId,
                activePhotoIndex,
                activePhoto!!,
                isSlideshowPlaying,
                showDetails = showDetailSheet,
                ratingState,
                exifState,
                commentState,
                setActiveIndex = { vm.setActiveIndex(it) },
                toggleSlideshow = { vm.toggleSlideshow() },
                toggleDetails = { vm.toggleShowDetails() },
                savePhotoToShare = { drawable, filename, onComplete -> vm.saveFileToShare(drawable, filename, onComplete) },
                navigateToYear,
                navigateToCategory
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RandomItemScreen(
    category: PhotoCategory,
    photos: List<Photo>,
    activePhotoId: Int,
    activePhotoIndex: Int,
    activePhoto: Photo,
    isSlideshowPlaying: Boolean,
    showDetails: Boolean,
    ratingState: RatingState,
    exifState: ExifState,
    commentState: CommentState,
    setActiveIndex: (index: Int) -> Unit,
    toggleSlideshow: () -> Unit,
    toggleDetails: () -> Unit,
    savePhotoToShare: (drawable: Drawable, filename: String, onComplete: (file: File) -> Unit) -> Unit,
    navigateToYear: (Int) -> Unit,
    navigateToCategory: (PhotoCategory) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    val rotationState = rememberRotation(activePhotoIndex)

    ItemPagerScaffold(
        showDetails = showDetails,
        topLeftContent = {
            OverlayYearName(
                category = category,
                onClickYear = { year -> navigateToYear(year) },
                onClickCategory = { category -> navigateToCategory(category) })
        },
        topRightContent = {
            OverlayPositionCount(
                position = activePhotoIndex + 1,
                count = photos.size
            )
        },
        bottomBarContent = {
            ButtonBar(
                isSlideshowPlaying = isSlideshowPlaying,
                onRotateLeft = { rotationState.setActiveRotation(-90f) },
                onRotateRight = { rotationState.setActiveRotation(90f) },
                onToggleSlideshow = toggleSlideshow,
                onShare = {
                    coroutineScope.launch {
                       sharePhoto(context, savePhotoToShare, activePhoto)
                    }
                },
                onViewDetails = toggleDetails
            )
        },
        detailSheetContent = {
            DetailBottomSheet(
                activePhotoId = activePhotoId,
                sheetState = sheetState,
                ratingState = ratingState,
                exifState = exifState,
                commentState = commentState,
                onDismissRequest = toggleDetails
            )
        }
    ) {
        PhotoPager(
            photos,
            activePhotoIndex,
            rotationState.activeRotation,
            setActiveIndex = { index -> setActiveIndex(index) }
        )
    }
}

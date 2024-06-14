package us.mikeandwan.photos.ui.screens.categoryItem

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import us.mikeandwan.photos.ui.PhotoListState
import us.mikeandwan.photos.ui.controls.scaffolds.ItemPagerScaffold
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
import us.mikeandwan.photos.ui.controls.photopager.PhotoPager
import us.mikeandwan.photos.ui.controls.photopager.rememberRotation
import us.mikeandwan.photos.ui.rememberPhotoListState
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

        val category by vm.category.collectAsStateWithLifecycle()
        val photos by vm.photos.collectAsStateWithLifecycle()
        val activePhotoId by vm.activeId.collectAsStateWithLifecycle()
        val activePhotoIndex by vm.activeIndex.collectAsStateWithLifecycle()
        val activePhoto by vm.activePhoto.collectAsStateWithLifecycle()
        val isSlideshowPlaying by vm.isSlideshowPlaying.collectAsStateWithLifecycle()
        val showDetailSheet by vm.showDetailSheet.collectAsStateWithLifecycle()
        val photoListState = rememberPhotoListState(
            category,
            photos,
            activePhotoId,
            activePhotoIndex,
            activePhoto,
            isSlideshowPlaying,
            showDetailSheet,
            setActiveIndex = { vm.setActiveIndex(it) },
            toggleSlideshow = { vm.toggleSlideshow() },
            toggleDetails = { vm.toggleShowDetails() },
            savePhotoToShare = { drawable, filename, onComplete -> vm.saveFileToShare(drawable, filename, onComplete) },
        )

        LaunchedEffect(Unit) {
            setNavArea(NavigationArea.Category)
        }

        LaunchedEffect(args.categoryId) {
            vm.loadCategory(args.categoryId)
            vm.loadPhotos(args.categoryId)
        }

        LaunchedEffect(photos, args.photoId) {
            if(photos.isNotEmpty() && args.photoId > 0) {
                vm.setActiveId(args.photoId)
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

        when(photoListState) {
            is PhotoListState.Loading -> Loading()
            is PhotoListState.CategoryLoaded -> {
                LaunchedEffect(photoListState.category) {
                    updateTopBar(true, true, photoListState.category.name)
                }

                Loading()
            }
            is PhotoListState.Loaded -> {
                LaunchedEffect(photoListState.category) {
                    updateTopBar(true, true, photoListState.category.name)
                }

                CategoryItemScreen(
                    photoListState,
                    ratingState,
                    exifState,
                    commentState
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryItemScreen(
    photoListState: PhotoListState.Loaded,
    ratingState: RatingState,
    exifState: ExifState,
    commentState: CommentState
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    val rotationState = rememberRotation(photoListState.activePhotoIndex)

    ItemPagerScaffold(
        showDetails = photoListState.showDetailSheet,
        topRightContent = {
            OverlayPositionCount(
                position = photoListState.activePhotoIndex + 1,
                count = photoListState.photos.size
            )
        },
        bottomBarContent = {
            ButtonBar(
                isSlideshowPlaying = photoListState.isSlideshowPlaying,
                onRotateLeft = { rotationState.setActiveRotation(-90f) },
                onRotateRight = { rotationState.setActiveRotation(90f) },
                onToggleSlideshow = photoListState.toggleSlideshow,
                onShare = {
                    coroutineScope.launch {
                        sharePhoto(context, photoListState.savePhotoToShare, photoListState.activePhoto!!)
                    }
                },
                onViewDetails = photoListState.toggleDetails
            )
        },
        detailSheetContent = {
            DetailBottomSheet(
                activePhotoId = photoListState.activePhotoId,
                sheetState = sheetState,
                ratingState = ratingState,
                exifState = exifState,
                commentState = commentState,
                onDismissRequest = photoListState.toggleDetails
            )
        }
    ) {
        PhotoPager(
            photoListState.photos,
            photoListState.activePhotoIndex,
            rotationState.activeRotation,
            setActiveIndex = { index -> photoListState.setActiveIndex(index) }
        )
    }
}

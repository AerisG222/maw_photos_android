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
import androidx.media3.datasource.HttpDataSource
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import us.mikeandwan.photos.domain.models.MediaType
import us.mikeandwan.photos.domain.models.NavigationArea
import us.mikeandwan.photos.ui.MediaListState
import us.mikeandwan.photos.ui.controls.scaffolds.ItemPagerScaffold
import us.mikeandwan.photos.ui.controls.loading.Loading
import us.mikeandwan.photos.ui.controls.metadata.CommentState
import us.mikeandwan.photos.ui.controls.metadata.DetailBottomSheet
import us.mikeandwan.photos.ui.controls.metadata.ExifState
import us.mikeandwan.photos.ui.controls.metadata.RatingState
import us.mikeandwan.photos.ui.controls.metadata.rememberCommentState
import us.mikeandwan.photos.ui.controls.metadata.rememberExifState
import us.mikeandwan.photos.ui.controls.metadata.rememberRatingState
import us.mikeandwan.photos.ui.controls.mediapager.ButtonBar
import us.mikeandwan.photos.ui.controls.mediapager.OverlayPositionCount
import us.mikeandwan.photos.ui.controls.mediapager.MediaPager
import us.mikeandwan.photos.ui.controls.mediapager.rememberRotation
import us.mikeandwan.photos.ui.rememberMediaListState
import us.mikeandwan.photos.ui.shareMedia

@Serializable
data class CategoryItemRoute (
    val mediaType: String,
    val categoryId: Int,
    val photoId: Int
)

fun NavGraphBuilder.categoryItemScreen(
    updateTopBar : (Boolean, Boolean, String) -> Unit,
    setNavArea: (NavigationArea) -> Unit,
    navigateToLogin: () -> Unit
) {
    composable<CategoryItemRoute> { backStackEntry ->
        val vm: CategoryItemViewModel = hiltViewModel()
        val args = backStackEntry.toRoute<CategoryItemRoute>()

        val category by vm.category.collectAsStateWithLifecycle()
        val media by vm.media.collectAsStateWithLifecycle()
        val activeId by vm.activeId.collectAsStateWithLifecycle()
        val activeIndex by vm.activeIndex.collectAsStateWithLifecycle()
        val activeMedia by vm.activeMedia.collectAsStateWithLifecycle()
        val isSlideshowPlaying by vm.isSlideshowPlaying.collectAsStateWithLifecycle()
        val showDetailSheet by vm.showDetailSheet.collectAsStateWithLifecycle()
        val photoListState = rememberMediaListState(
            category,
            media,
            activeId,
            activeIndex,
            activeMedia,
            isSlideshowPlaying,
            showDetailSheet,
            setActiveIndex = { vm.setActiveIndex(it) },
            toggleSlideshow = { vm.toggleSlideshow() },
            toggleDetails = { vm.toggleShowDetails() },
            saveMediaToShare = { drawable, filename, onComplete -> vm.saveFileToShare(drawable, filename, onComplete) },
        )

        LaunchedEffect(Unit) {
            setNavArea(NavigationArea.Category)
        }

        LaunchedEffect(args.categoryId) {
            val type = MediaType.valueOf(args.mediaType)

            vm.loadCategory(type, args.categoryId)
            vm.loadMedia(type, args.categoryId)
        }

        LaunchedEffect(media, args.photoId) {
            if(media.isNotEmpty() && args.photoId > 0) {
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
            is MediaListState.Loading -> Loading()
            is MediaListState.CategoryLoaded -> {
                LaunchedEffect(photoListState.category) {
                    updateTopBar(true, true, photoListState.category.name)
                }

                Loading()
            }
            is MediaListState.Loaded -> {
                LaunchedEffect(photoListState.category) {
                    updateTopBar(true, true, photoListState.category.name)
                }

                CategoryItemScreen(
                    photoListState,
                    ratingState,
                    exifState,
                    commentState,
                    vm.videoPlayerDataSourceFactory
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryItemScreen(
    mediaListState: MediaListState.Loaded,
    ratingState: RatingState,
    exifState: ExifState,
    commentState: CommentState,
    videoPlayerDataSourceFactory: HttpDataSource.Factory
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    val rotationState = rememberRotation(mediaListState.activeIndex)

    ItemPagerScaffold(
        showDetails = mediaListState.showDetailSheet,
        topRightContent = {
            OverlayPositionCount(
                position = mediaListState.activeIndex + 1,
                count = mediaListState.media.size
            )
        },
        bottomBarContent = {
            ButtonBar(
                activeMediaType = mediaListState.activeMedia!!.type,
                isSlideshowPlaying = mediaListState.isSlideshowPlaying,
                onRotateLeft = { rotationState.setActiveRotation(-90f) },
                onRotateRight = { rotationState.setActiveRotation(90f) },
                onToggleSlideshow = mediaListState.toggleSlideshow,
                onShare = {
                    coroutineScope.launch {
                        shareMedia(context, mediaListState.saveMediaToShare, mediaListState.activeMedia)
                    }
                },
                onViewDetails = mediaListState.toggleDetails
            )
        },
        detailSheetContent = {
            DetailBottomSheet(
                activeMedia = mediaListState.activeMedia!!,
                sheetState = sheetState,
                ratingState = ratingState,
                exifState = exifState,
                commentState = commentState,
                onDismissRequest = mediaListState.toggleDetails
            )
        }
    ) {
        MediaPager(
            mediaListState.media,
            mediaListState.activeIndex,
            rotationState.activeRotation,
            videoPlayerDataSourceFactory,
            setActiveIndex = { index -> mediaListState.setActiveIndex(index) }
        )
    }
}

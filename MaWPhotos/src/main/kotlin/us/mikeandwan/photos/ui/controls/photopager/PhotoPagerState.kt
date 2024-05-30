package us.mikeandwan.photos.ui.controls.photopager

import android.graphics.drawable.Drawable
import androidx.compose.runtime.Composable
import us.mikeandwan.photos.domain.models.Photo
import us.mikeandwan.photos.domain.models.PhotoCategory
import us.mikeandwan.photos.ui.controls.metadata.CommentState
import us.mikeandwan.photos.ui.controls.metadata.ExifState
import us.mikeandwan.photos.ui.controls.metadata.RatingState
import java.io.File

data class PhotoPagerState (
    val category: PhotoCategory?,
    val photos: List<Photo>,
    val activePhotoId: Int,
    val activePhotoIndex: Int,
    val isSlideshowPlaying: Boolean,
    val showDetails: Boolean,
    val showPositionAndCount: Boolean,
    val showYearAndCategory: Boolean,
    val ratingState: RatingState,
    val exifState: ExifState,
    val commentState: CommentState,
    val updateCurrentPhoto: (photoId: Int) -> Unit,
    val toggleSlideshow: () -> Unit,
    val savePhotoToShare: (drawable: Drawable, filename: String, onComplete: (file: File) -> Unit) -> Unit,
    val toggleDetails: () -> Unit
)

@Composable
fun rememberPhotoPagerState(
    category: PhotoCategory? = null,
    photos: List<Photo> = emptyList(),
    activePhotoId: Int,
    activePhotoIndex: Int,
    isSlideshowPlaying: Boolean,
    showDetails: Boolean,
    showPositionAndCount: Boolean,
    showYearAndCategory: Boolean,
    ratingState: RatingState,
    exifState: ExifState,
    commentState: CommentState,
    updateCurrentPhoto: (photoId: Int) -> Unit,
    toggleSlideshow: () -> Unit,
    savePhotoToShare: (drawable: Drawable, filename: String, onComplete: (file: File) -> Unit) -> Unit,
    toggleDetails: () -> Unit
): PhotoPagerState {
    return PhotoPagerState(
        category,
        photos,
        activePhotoId,
        activePhotoIndex,
        isSlideshowPlaying,
        showDetails,
        showPositionAndCount,
        showYearAndCategory,
        ratingState,
        exifState,
        commentState,
        updateCurrentPhoto,
        toggleSlideshow,
        savePhotoToShare,
        toggleDetails
    )
}

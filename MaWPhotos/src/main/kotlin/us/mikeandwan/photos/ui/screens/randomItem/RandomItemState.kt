package us.mikeandwan.photos.ui.screens.randomItem

import android.graphics.drawable.Drawable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import us.mikeandwan.photos.domain.models.Photo
import us.mikeandwan.photos.domain.models.PhotoCategory
import us.mikeandwan.photos.ui.controls.metadata.CommentState
import us.mikeandwan.photos.ui.controls.metadata.ExifState
import us.mikeandwan.photos.ui.controls.metadata.RatingState
import us.mikeandwan.photos.ui.controls.metadata.rememberCommentState
import us.mikeandwan.photos.ui.controls.metadata.rememberExifState
import us.mikeandwan.photos.ui.controls.metadata.rememberRatingState
import java.io.File

sealed class RandomItemState {
    data object Loading: RandomItemState()

    data class Loaded(
        val category: PhotoCategory,
        val photos: List<Photo>,
        val activePhotoId: Int,
        val activePhotoIndex: Int,
        val isSlideshowPlaying: Boolean,
        val showDetails: Boolean,
        val ratingState: RatingState,
        val exifState: ExifState,
        val commentState: CommentState,
        val setActiveId: (id: Int) -> Unit,
        val setActiveIndex: (index: Int) -> Unit,
        val toggleSlideshow: () -> Unit,
        val savePhotoToShare: (drawable: Drawable, filename: String, onComplete: (file: File) -> Unit) -> Unit,
        val toggleDetails: () -> Unit
    ): RandomItemState()
}

@Composable
fun rememberRandomItemState(
    vm: RandomItemViewModel,
    photoId: Int
): RandomItemState {
    val category by vm.category.collectAsStateWithLifecycle()
    val photos by vm.photos.collectAsStateWithLifecycle()
    val activePhotoId by vm.activeId.collectAsStateWithLifecycle()
    val activePhotoIndex by vm.activeIndex.collectAsStateWithLifecycle()
    val activePhoto by vm.activePhoto.collectAsStateWithLifecycle()
    val isSlideshowPlaying by vm.isSlideshowPlaying.collectAsStateWithLifecycle()
    val showDetailSheet by vm.showDetailSheet.collectAsStateWithLifecycle()

    LaunchedEffect(photos, photoId) {
        if(photos.isNotEmpty() && photoId > 0) {
            vm.setActiveId(photoId)
        }
    }

    LaunchedEffect(activePhoto) {
        if(activePhoto != null) {
            vm.loadCategory(activePhoto!!.categoryId)
        }
    }

    val userRating by vm.userRating.collectAsStateWithLifecycle()
    val averageRating by vm.averageRating.collectAsStateWithLifecycle()

    val ratingState = rememberRatingState(
        userRating = userRating,
        averageRating = averageRating,
        fetchRating = { vm.fetchRatingDetails() },
        updateUserRating = { vm.setRating(it) }
    )

    val exif by vm.exif.collectAsStateWithLifecycle()

    val exifState = rememberExifState(
        exif,
        fetchExif = { vm.fetchExifDetails() }
    )

    val comments by vm.comments.collectAsStateWithLifecycle()

    val commentState = rememberCommentState(
        comments = comments,
        fetchComments = { vm.fetchCommentDetails() },
        addComment = { vm.addComment(it) }
    )

    fun setActiveId(newId: Int) {
        vm.setActiveId(newId)
    }

    fun setActiveIndex(index: Int) {
        vm.setActiveIndex(index)
    }

    fun toggleSlideshow() {
        vm.toggleSlideshow()
    }

    fun toggleShowDetails() {
        vm.toggleShowDetails()
    }

    fun savePhotoToShare(drawable: Drawable, filename: String, onComplete: (File) -> Unit) {
        vm.saveFileToShare(drawable, filename, onComplete)
    }

    return if(category == null) {
        RandomItemState.Loading
    } else {
        RandomItemState.Loaded(
            category!!,
            photos,
            activePhotoId,
            activePhotoIndex,
            isSlideshowPlaying,
            showDetails = showDetailSheet,
            ratingState,
            exifState,
            commentState,
            setActiveId = { setActiveId(it) },
            setActiveIndex = { setActiveIndex(it) },
            toggleSlideshow = { toggleSlideshow() },
            toggleDetails = { toggleShowDetails() },
            savePhotoToShare = { drawable, filename, onComplete -> savePhotoToShare(drawable, filename, onComplete) }
        )
    }
}

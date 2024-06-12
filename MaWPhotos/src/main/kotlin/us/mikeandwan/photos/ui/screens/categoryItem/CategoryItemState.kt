package us.mikeandwan.photos.ui.screens.categoryItem

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

sealed class CategoryItemState {
    data object Loading: CategoryItemState()

    data class CategoryLoaded(
        val category: PhotoCategory
    ): CategoryItemState()

    data class Loaded(
        val category: PhotoCategory,
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
    ): CategoryItemState()
}

@Composable
fun rememberCategoryItemState(
    vm: CategoryItemViewModel,
    categoryId: Int,
    photoId: Int
): CategoryItemState {
    LaunchedEffect(categoryId) {
        vm.loadCategory(categoryId)
        vm.loadPhotos(categoryId)
    }

    val category by vm.category.collectAsStateWithLifecycle()
    val photos by vm.photos.collectAsStateWithLifecycle()
    val activePhotoId by vm.activePhotoId.collectAsStateWithLifecycle()
    val activePhotoIndex by vm.activePhotoIndex.collectAsStateWithLifecycle()
    val isSlideshowPlaying by vm.isSlideshowPlaying.collectAsStateWithLifecycle()
    val showDetailSheet by vm.showDetailSheet.collectAsStateWithLifecycle()

    LaunchedEffect(photos, photoId) {
        if(photos.isNotEmpty() && photoId > 0) {
            vm.setActivePhotoId(photoId)
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

    fun updateActivePhoto(newPhotoId: Int) {
        vm.setActivePhotoId(newPhotoId)
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
        CategoryItemState.Loading
    } else if(photos.isEmpty() || activePhotoIndex < 0) {
        CategoryItemState.CategoryLoaded(category!!)
    } else {
        CategoryItemState.Loaded(
            category!!,
            photos,
            activePhotoId,
            activePhotoIndex,
            isSlideshowPlaying,
            showDetails = showDetailSheet,
            showPositionAndCount = true,
            showYearAndCategory = false,
            ratingState,
            exifState,
            commentState,
            updateCurrentPhoto = { updateActivePhoto(it) },
            toggleSlideshow = { toggleSlideshow() },
            toggleDetails = { toggleShowDetails() },
            savePhotoToShare = { drawable, filename, onComplete -> savePhotoToShare(drawable, filename, onComplete) }
        )
    }
}
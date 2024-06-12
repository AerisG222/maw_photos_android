package us.mikeandwan.photos.ui.screens.randomItem

import android.graphics.drawable.Drawable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import us.mikeandwan.photos.domain.models.Photo
import us.mikeandwan.photos.ui.controls.metadata.rememberCommentState
import us.mikeandwan.photos.ui.controls.metadata.rememberExifState
import us.mikeandwan.photos.ui.controls.metadata.rememberRatingState
import java.io.File

sealed class RandomItemState {
    data object Loading: RandomItemState()

    data class Loaded(
        val photos: List<Photo>
    ): RandomItemState()
}

@Composable
fun rememberRandomItemState(
    vm: RandomItemViewModel,
    photoId: Int
): RandomItemState {
    val category by vm.category.collectAsStateWithLifecycle()
    val photos by vm.photos.collectAsStateWithLifecycle()
    val activePhotoId by vm.activePhotoId.collectAsStateWithLifecycle()
    val activePhotoIndex by vm.activePhotoIndex.collectAsStateWithLifecycle()
    val activePhoto by vm.activePhoto.collectAsStateWithLifecycle()
    val isSlideshowPlaying by vm.isSlideshowPlaying.collectAsStateWithLifecycle()
    val showDetailSheet by vm.showDetailSheet.collectAsStateWithLifecycle()

    LaunchedEffect(photos, photoId) {
        if(photos.isNotEmpty() && photoId > 0) {
            vm.setActivePhotoId(photoId)
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

//    val pagerState = rememberPhotoPagerState(
//        category,
//        photos,
//        activePhotoId,
//        activePhotoIndex,
//        isSlideshowPlaying,
//        showDetails = showDetailSheet,
//        showPositionAndCount = true,
//        showYearAndCategory = true,
//        ratingState,
//        exifState,
//        commentState,
//        updateCurrentPhoto = { updateActivePhoto(it) },
//        toggleSlideshow = { toggleSlideshow() },
//        toggleDetails = { toggleShowDetails() },
//        savePhotoToShare = { drawable, filename, onComplete -> savePhotoToShare(drawable, filename, onComplete) }
//    )

    return if(category == null) {
        RandomItemState.Loading
    } else {
        RandomItemState.Loaded(
            photos
        )
    }
}

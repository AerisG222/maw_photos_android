package us.mikeandwan.photos.ui.screens.category

import android.graphics.drawable.Drawable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import us.mikeandwan.photos.domain.models.PhotoCategory
import us.mikeandwan.photos.ui.controls.imagegrid.ImageGridState
import us.mikeandwan.photos.ui.controls.imagegrid.rememberImageGridState
import us.mikeandwan.photos.ui.controls.metadata.rememberCommentState
import us.mikeandwan.photos.ui.controls.metadata.rememberExifState
import us.mikeandwan.photos.ui.controls.metadata.rememberRatingState
import us.mikeandwan.photos.ui.controls.photopager.PhotoPagerState
import us.mikeandwan.photos.ui.controls.photopager.rememberPhotoPagerState
import java.io.File

sealed class CategoryState {
    data object Loading: CategoryState()

    data class CategoryLoaded(
        val category: PhotoCategory
    ): CategoryState()

    data class Loaded(
        val category: PhotoCategory,
        val showImageGrid: Boolean,
        val gridState: ImageGridState,
        val photoPagerState: PhotoPagerState
    ): CategoryState()
}

@Composable
fun rememberCategoryState(
    vm: CategoryViewModel,
    categoryId: Int,
    photoId: Int
): CategoryState {
    LaunchedEffect(categoryId) {
        vm.loadCategory(categoryId)
        vm.loadPhotos(categoryId)
    }

    val category by vm.category.collectAsStateWithLifecycle()
    val photos by vm.photos.collectAsStateWithLifecycle()
    val gridItems by vm.gridItems.collectAsStateWithLifecycle()
    val thumbSize by vm.gridItemThumbnailSize.collectAsStateWithLifecycle()
    val activePhotoId by vm.activePhotoId.collectAsStateWithLifecycle()
    val activePhotoIndex by vm.activePhotoIndex.collectAsStateWithLifecycle()
    val isSlideshowPlaying by vm.isSlideshowPlaying.collectAsStateWithLifecycle()
    val showDetailSheet by vm.showDetailSheet.collectAsStateWithLifecycle()

    val showImageGrid = activePhotoId <= 0 || activePhotoIndex < 0

    LaunchedEffect(category, photos, photoId) {
        if(photos.isNotEmpty() && photoId > 0) {
            vm.setActivePhotoId(photoId)
        }
    }

    val gridState = rememberImageGridState(
        gridItems = gridItems,
        thumbnailSize = thumbSize,
        onSelectGridItem = { photo -> vm.setActivePhotoId(photo.id) }
    )

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

    val pagerState = rememberPhotoPagerState(
        category,
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

    return if(category == null) {
        CategoryState.Loading
    } else if(photos.isEmpty()) {
        CategoryState.CategoryLoaded(category!!)
    } else {
        CategoryState.Loaded(
            category!!,
            showImageGrid,
            gridState,
            pagerState
        )
    }
}

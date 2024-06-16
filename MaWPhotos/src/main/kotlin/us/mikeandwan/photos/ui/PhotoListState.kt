package us.mikeandwan.photos.ui

import android.graphics.drawable.Drawable
import androidx.compose.runtime.Composable
import us.mikeandwan.photos.domain.models.Photo
import us.mikeandwan.photos.domain.models.MediaCategory
import java.io.File

sealed class PhotoListState {
    data object Loading: PhotoListState()

    data class CategoryLoaded(
        val category: MediaCategory
    ): PhotoListState()

    data class Loaded(
        val category: MediaCategory,
        val photos: List<Photo>,
        val activePhotoId: Int,
        val activePhotoIndex: Int,
        val activePhoto: Photo?,
        val isSlideshowPlaying: Boolean,
        val showDetailSheet: Boolean,
        val setActiveIndex: (index: Int) -> Unit,
        val toggleSlideshow: () -> Unit,
        val toggleDetails: () -> Unit,
        val savePhotoToShare: (drawable: Drawable, filename: String, onComplete: (file: File) -> Unit) -> Unit
    ): PhotoListState()
}

@Composable
fun rememberPhotoListState(
    category: MediaCategory?,
    photos: List<Photo>,
    activePhotoId: Int,
    activePhotoIndex: Int,
    activePhoto: Photo?,
    isSlideshowPlaying: Boolean,
    showDetailSheet: Boolean,
    setActiveIndex: (index: Int) -> Unit,
    toggleSlideshow: () -> Unit,
    toggleDetails: () -> Unit,
    savePhotoToShare: (drawable: Drawable, filename: String, onComplete: (file: File) -> Unit) -> Unit
): PhotoListState {
    if(category == null || photos.isEmpty()) {
        return PhotoListState.Loading
    }

    if((category != null && photos.isEmpty()) || activePhotoIndex < 0) {
        return PhotoListState.CategoryLoaded(category)
    }

    return PhotoListState.Loaded(
        category,
        photos,
        activePhotoId,
        activePhotoIndex,
        activePhoto,
        isSlideshowPlaying,
        showDetailSheet,
        setActiveIndex,
        toggleSlideshow,
        toggleDetails,
        savePhotoToShare
    )
}

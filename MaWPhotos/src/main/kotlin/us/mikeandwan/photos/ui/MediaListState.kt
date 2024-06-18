package us.mikeandwan.photos.ui

import android.graphics.drawable.Drawable
import androidx.compose.runtime.Composable
import us.mikeandwan.photos.domain.models.Media
import us.mikeandwan.photos.domain.models.MediaCategory
import java.io.File

sealed class MediaListState {
    data object Loading: MediaListState()

    data class CategoryLoaded(
        val category: MediaCategory
    ): MediaListState()

    data class Loaded(
        val category: MediaCategory,
        val media: List<Media>,
        val activeId: Int,
        val activeIndex: Int,
        val activeMedia: Media?,
        val isSlideshowPlaying: Boolean,
        val showDetailSheet: Boolean,
        val setActiveIndex: (index: Int) -> Unit,
        val toggleSlideshow: () -> Unit,
        val toggleDetails: () -> Unit,
        val saveMediaToShare: (drawable: Drawable, filename: String, onComplete: (file: File) -> Unit) -> Unit
    ): MediaListState()
}

@Composable
fun rememberMediaListState(
    category: MediaCategory?,
    media: List<Media>,
    activeId: Int,
    activeIndex: Int,
    activeMedia: Media?,
    isSlideshowPlaying: Boolean,
    showDetailSheet: Boolean,
    setActiveIndex: (index: Int) -> Unit,
    toggleSlideshow: () -> Unit,
    toggleDetails: () -> Unit,
    saveMediaToShare: (drawable: Drawable, filename: String, onComplete: (file: File) -> Unit) -> Unit
): MediaListState {
    if(category == null || media.isEmpty()) {
        return MediaListState.Loading
    }

    if((category != null && media.isEmpty()) || activeIndex < 0) {
        return MediaListState.CategoryLoaded(category)
    }

    return MediaListState.Loaded(
        category,
        media,
        activeId,
        activeIndex,
        activeMedia,
        isSlideshowPlaying,
        showDetailSheet,
        setActiveIndex,
        toggleSlideshow,
        toggleDetails,
        saveMediaToShare
    )
}

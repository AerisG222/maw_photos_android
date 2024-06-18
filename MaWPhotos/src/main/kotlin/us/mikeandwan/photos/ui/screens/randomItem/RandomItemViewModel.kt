package us.mikeandwan.photos.ui.screens.randomItem

import android.graphics.drawable.Drawable
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import us.mikeandwan.photos.domain.RandomPhotoRepository
import us.mikeandwan.photos.domain.RandomPreferenceRepository
import us.mikeandwan.photos.domain.models.RANDOM_PREFERENCE_DEFAULT
import us.mikeandwan.photos.domain.services.MediaListService
import us.mikeandwan.photos.ui.screens.random.BaseRandomViewModel
import java.io.File
import javax.inject.Inject

@HiltViewModel
class RandomItemViewModel @Inject constructor(
    randomPhotoRepository: RandomPhotoRepository,
    randomPreferenceRepository: RandomPreferenceRepository,
    private val mediaListService: MediaListService
) : BaseRandomViewModel(
    randomPhotoRepository
) {
    val category = mediaListService.category
    val activePhoto = mediaListService.activeMedia
    val activeId = mediaListService.activeId
    val activeIndex = mediaListService.activeIndex
    val isSlideshowPlaying = mediaListService.isSlideshowPlaying
    val showDetailSheet = mediaListService.showDetailSheet

    fun setActiveId(id: Int) { mediaListService.setActiveId(id) }
    fun setActiveIndex(index: Int) { mediaListService.setActiveIndex(index) }
    fun toggleSlideshow() { mediaListService.toggleSlideshow() }
    fun toggleShowDetails() { mediaListService.toggleShowDetails() }

    fun saveFileToShare(drawable: Drawable, filename: String, onComplete: (File) -> Unit) {
        mediaListService.saveFileToShare(drawable, filename, onComplete)
    }

    // ratings
    val userRating = mediaListService.userRating
    val averageRating = mediaListService.averageRating
    fun setRating(rating: Short) { mediaListService.setRating(rating) }
    fun fetchRatingDetails() { mediaListService.fetchRating() }

    // exif
    val exif = mediaListService.exif
    fun fetchExif() { mediaListService.fetchExif() }

    // comments
    val comments = mediaListService.comments
    fun fetchCommentDetails() { mediaListService.fetchComments() }
    fun addComment(comment: String) { mediaListService.addComment(comment) }

    private val slideshowDurationInMillis = randomPreferenceRepository
        .getSlideshowIntervalSeconds()
        .map { seconds -> (seconds * 1000).toLong() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, (RANDOM_PREFERENCE_DEFAULT.slideshowIntervalSeconds * 1000).toLong())

    init {
        mediaListService.initialize(
            photos,
            slideshowDurationInMillis
        )
    }
}

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
import us.mikeandwan.photos.domain.services.PhotoListService
import us.mikeandwan.photos.ui.screens.random.BaseRandomViewModel
import java.io.File
import javax.inject.Inject

@HiltViewModel
class RandomItemViewModel @Inject constructor(
    randomPhotoRepository: RandomPhotoRepository,
    randomPreferenceRepository: RandomPreferenceRepository,
    private val photoListService: PhotoListService
) : BaseRandomViewModel(
    randomPhotoRepository
) {
    val category = photoListService.category
    val activePhoto = photoListService.activePhoto
    val activeId = photoListService.activeId
    val activeIndex = photoListService.activeIndex
    val isSlideshowPlaying = photoListService.isSlideshowPlaying
    val showDetailSheet = photoListService.showDetailSheet

    fun setActiveId(id: Int) { photoListService.setActiveId(id) }
    fun setActiveIndex(index: Int) { photoListService.setActiveIndex(index) }
    fun toggleSlideshow() { photoListService.toggleSlideshow() }
    fun toggleShowDetails() { photoListService.toggleShowDetails() }

    fun saveFileToShare(drawable: Drawable, filename: String, onComplete: (File) -> Unit) {
        photoListService.saveFileToShare(drawable, filename, onComplete)
    }

    // ratings
    val userRating = photoListService.userRating
    val averageRating = photoListService.averageRating
    fun setRating(rating: Short) { photoListService.setRating(rating) }
    fun fetchRatingDetails() { photoListService.fetchRating() }

    // exif
    val exif = photoListService.exif
    fun fetchExif() { photoListService.fetchExif() }

    // comments
    val comments = photoListService.comments
    fun fetchCommentDetails() { photoListService.fetchComments() }
    fun addComment(comment: String) { photoListService.addComment(comment) }

    private val slideshowDurationInMillis = randomPreferenceRepository
        .getSlideshowIntervalSeconds()
        .map { seconds -> (seconds * 1000).toLong() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, (RANDOM_PREFERENCE_DEFAULT.slideshowIntervalSeconds * 1000).toLong())

    init {
        photoListService.initialize(
            photos,
            slideshowDurationInMillis
        )
    }
}

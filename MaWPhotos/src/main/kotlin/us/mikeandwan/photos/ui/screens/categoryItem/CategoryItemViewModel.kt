package us.mikeandwan.photos.ui.screens.categoryItem

import android.graphics.drawable.Drawable
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import us.mikeandwan.photos.domain.PhotoCategoryRepository
import us.mikeandwan.photos.domain.PhotoPreferenceRepository
import us.mikeandwan.photos.domain.models.PHOTO_PREFERENCE_DEFAULT
import us.mikeandwan.photos.domain.services.PhotoListService
import us.mikeandwan.photos.ui.screens.category.BaseCategoryViewModel
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CategoryItemViewModel @Inject constructor (
    photoCategoryRepository: PhotoCategoryRepository,
    photoPreferenceRepository: PhotoPreferenceRepository,
    private val photoListService: PhotoListService
) : BaseCategoryViewModel(
    photoCategoryRepository
) {
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

    private val slideshowDurationInMillis = photoPreferenceRepository
        .getSlideshowIntervalSeconds()
        .map { seconds -> (seconds * 1000).toLong() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, (PHOTO_PREFERENCE_DEFAULT.slideshowIntervalSeconds * 1000).toLong())

    init {
        photoListService.initialize(
            photos,
            slideshowDurationInMillis
        )
    }
}

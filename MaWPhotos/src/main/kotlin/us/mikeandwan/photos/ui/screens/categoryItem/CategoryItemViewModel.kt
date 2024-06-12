package us.mikeandwan.photos.ui.screens.categoryItem

import android.graphics.drawable.Drawable
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import us.mikeandwan.photos.domain.FileStorageRepository
import us.mikeandwan.photos.domain.PeriodicJob
import us.mikeandwan.photos.domain.PhotoCategoryRepository
import us.mikeandwan.photos.domain.PhotoPreferenceRepository
import us.mikeandwan.photos.domain.models.RANDOM_PREFERENCE_DEFAULT
import us.mikeandwan.photos.domain.services.PhotoCommentService
import us.mikeandwan.photos.domain.services.PhotoExifService
import us.mikeandwan.photos.domain.services.PhotoRatingService
import us.mikeandwan.photos.ui.screens.category.BaseCategoryViewModel
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CategoryItemViewModel @Inject constructor (
    photoCategoryRepository: PhotoCategoryRepository,
    private val fileRepository: FileStorageRepository,
    private val photoRatingService: PhotoRatingService,
    private val photoCommentService: PhotoCommentService,
    private val photoExifService: PhotoExifService,
    photoPreferenceRepository: PhotoPreferenceRepository
) : BaseCategoryViewModel(
    photoCategoryRepository
) {
    private var slideshowJob: PeriodicJob<Unit>

    private val _resumeSlideshowAfterShowingDetails = MutableStateFlow(false)

    private val _showDetailSheet = MutableStateFlow(false)
    val showDetailSheet = _showDetailSheet.asStateFlow()

    private val slideshowDurationInMillis = photoPreferenceRepository
        .getSlideshowIntervalSeconds()
        .map { seconds -> (seconds * 1000).toLong() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, (RANDOM_PREFERENCE_DEFAULT.slideshowIntervalSeconds * 1000).toLong())

    var isSlideshowPlaying: StateFlow<Boolean>

    private val _activePhotoId = MutableStateFlow(-1)
    val activePhotoId = _activePhotoId.asStateFlow()

    val activePhotoIndex = photos.combine(activePhotoId) { photos, activePhotoId ->
        photos.indexOfFirst { it.id == activePhotoId }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, -1)

    fun setActivePhotoId(photoId: Int) {
        _activePhotoId.value = photoId
    }

    fun toggleSlideshow() {
        if(slideshowJob.doJob.value) {
            slideshowJob.stop()
        } else {
            slideshowJob.start()
        }
    }

    fun toggleShowDetails() {
        if(_showDetailSheet.value) {
            // detail sheet to be hidden
            if(_resumeSlideshowAfterShowingDetails.value) {
                slideshowJob.start()
            }
        } else {
            _resumeSlideshowAfterShowingDetails.value = isSlideshowPlaying.value
            slideshowJob.stop()
        }

        _showDetailSheet.value = !_showDetailSheet.value
    }

    fun saveFileToShare(drawable: Drawable, filename: String, onComplete: (File) -> Unit) {
        viewModelScope.launch {
            val file = fileRepository.savePhotoToShare(drawable, filename)

            onComplete(file)
        }
    }

    private fun moveNext() = flow<Unit>{
        if(activePhotoIndex.value >= photos.value.size) {
            slideshowJob.stop()
        } else {
            setActivePhotoId(photos.value[activePhotoIndex.value + 1].id)
        }
    }

    // RATINGS
    val userRating = photoRatingService.userRating
    val averageRating = photoRatingService.averageRating

    fun setRating(rating: Short) {
        viewModelScope.launch {
            photoRatingService.setRating(activePhotoId.value, rating)
        }
    }

    fun fetchRatingDetails() {
        viewModelScope.launch {
            photoRatingService.fetchRatingDetails(activePhotoId.value)
        }
    }

    // EXIF
    val exif = photoExifService.exif

    fun fetchExifDetails() {
        viewModelScope.launch {
            photoExifService.fetchExifDetails(activePhotoId.value)
        }
    }

    // COMMENTS
    val comments = photoCommentService.comments

    fun fetchCommentDetails() {
        viewModelScope.launch {
            photoCommentService.fetchCommentDetails(activePhotoId.value)
        }
    }

    fun addComment(comment: String) {
        viewModelScope.launch {
            photoCommentService.addComment(activePhotoId.value, comment)
        }
    }

    init {
        slideshowJob = PeriodicJob(
            false,
            slideshowDurationInMillis.value
        ) { moveNext() }

        isSlideshowPlaying = slideshowJob.doJob
    }
}
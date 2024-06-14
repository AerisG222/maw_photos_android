package us.mikeandwan.photos.domain.services

import android.graphics.drawable.Drawable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import us.mikeandwan.photos.domain.FileStorageRepository
import us.mikeandwan.photos.domain.PeriodicJob
import us.mikeandwan.photos.domain.PhotoCategoryRepository
import us.mikeandwan.photos.domain.models.Photo
import us.mikeandwan.photos.domain.models.PhotoCategory
import java.io.File
import javax.inject.Inject

class PhotoListService @Inject constructor (
    private val photoCategoryRepository: PhotoCategoryRepository,
    private val fileRepository: FileStorageRepository,
    private val photoRatingService: PhotoRatingService,
    private val photoCommentService: PhotoCommentService,
    private val photoExifService: PhotoExifService
) {
    private lateinit var _viewModelScope: CoroutineScope
    private lateinit var _photos: StateFlow<List<Photo>>
    private lateinit var _slideshowDurationInMillis: StateFlow<Long>
    private lateinit var _slideshowJob: PeriodicJob<Unit>

    private val _resumeSlideshowAfterShowingDetails = MutableStateFlow(false)
    val isSlideshowPlaying: StateFlow<Boolean> = _slideshowJob.doJob

    private val _showDetailSheet = MutableStateFlow(false)
    val showDetailSheet = _showDetailSheet.asStateFlow()

    private val _category = MutableStateFlow<PhotoCategory?>(null)
    val category = _category.asStateFlow()

    private val _activeIndex = MutableStateFlow(-1)
    val activeIndex = _activeIndex.asStateFlow()

    val activePhoto = _photos.combine(activeIndex) { photos, index ->
        if(index >= 0 && index < photos.size) {
            photos[index]
        } else {
            null
        }
    }.stateIn(_viewModelScope, SharingStarted.Eagerly, null)

    val activeId = activePhoto
        .map { photo -> photo?.id ?: -1 }
        .stateIn(_viewModelScope, SharingStarted.Eagerly, -1)

    fun setActiveIndex(index: Int) {
        _activeIndex.value = index
    }

    fun setActiveId(id: Int) {
        setActiveIndex(_photos.value.indexOfFirst { it.id == id })
    }

    fun toggleSlideshow() {
        if(_slideshowJob.doJob.value) {
            _slideshowJob.stop()
        } else {
            _slideshowJob.start()
        }
    }

    fun toggleShowDetails() {
        if(_showDetailSheet.value) {
            // detail sheet to be hidden
            if(_resumeSlideshowAfterShowingDetails.value) {
                _slideshowJob.start()
            }
        } else {
            _resumeSlideshowAfterShowingDetails.value = isSlideshowPlaying.value
            _slideshowJob.stop()
        }

        _showDetailSheet.value = !_showDetailSheet.value
    }

    fun saveFileToShare(drawable: Drawable, filename: String, onComplete: (File) -> Unit) {
        _viewModelScope.launch {
            val file = fileRepository.savePhotoToShare(drawable, filename)

            onComplete(file)
        }
    }

    // RATINGS
    val userRating = photoRatingService.userRating
    val averageRating = photoRatingService.averageRating

    fun setRating(rating: Short) {
        _viewModelScope.launch {
            photoRatingService.setRating(activeId.value, rating)
        }
    }

    fun fetchRating() {
        _viewModelScope.launch {
            photoRatingService.fetchRatingDetails(activeId.value)
        }
    }

    // EXIF
    val exif = photoExifService.exif

    fun fetchExif() {
        _viewModelScope.launch {
            photoExifService.fetchExifDetails(activeId.value)
        }
    }

    // COMMENTS
    val comments = photoCommentService.comments

    fun fetchComments() {
        _viewModelScope.launch {
            photoCommentService.fetchCommentDetails(activeId.value)
        }
    }

    fun addComment(comment: String) {
        _viewModelScope.launch {
            photoCommentService.addComment(activeId.value, comment)
        }
    }

    fun initialize(
        photos: StateFlow<List<Photo>>,
        slideshowDurationInMillis: StateFlow<Long>,
        viewModelScope: CoroutineScope
    ) {
        _photos = photos
        _slideshowDurationInMillis = slideshowDurationInMillis
        _viewModelScope = viewModelScope

        _slideshowJob = PeriodicJob(
            false,
            slideshowDurationInMillis.value
        ) { moveNext() }

        viewModelScope.launch {
            activePhoto
                .filter { it != null }
                .collect { loadCategory(it!!.categoryId) }
        }

        viewModelScope.launch {
            _slideshowDurationInMillis
                .collect { _slideshowJob.setIntervalMillis(it) }
        }
    }

    private fun loadCategory(categoryId: Int) {
        if(category.value?.id == categoryId) {
            return
        }

        _category.value = null

        _viewModelScope.launch {
            photoCategoryRepository
                .getCategory(categoryId)
                .collect { _category.value = it }
        }
    }

    private fun moveNext() = flow<Unit>{
        if(activeIndex.value >= _photos.value.size) {
            _slideshowJob.stop()
        } else {
            setActiveIndex(activeIndex.value + 1)
        }
    }
}

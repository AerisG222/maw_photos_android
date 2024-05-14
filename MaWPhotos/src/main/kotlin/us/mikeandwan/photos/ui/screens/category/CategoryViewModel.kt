package us.mikeandwan.photos.ui.screens.category

import android.graphics.drawable.Drawable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import us.mikeandwan.photos.domain.FileStorageRepository
import us.mikeandwan.photos.domain.PeriodicJob
import us.mikeandwan.photos.domain.PhotoCategoryRepository
import us.mikeandwan.photos.domain.PhotoPreferenceRepository
import us.mikeandwan.photos.domain.PhotoRepository
import us.mikeandwan.photos.domain.models.GridThumbnailSize
import us.mikeandwan.photos.domain.models.ExternalCallStatus
import us.mikeandwan.photos.domain.models.Photo
import us.mikeandwan.photos.domain.models.PhotoCategory
import us.mikeandwan.photos.domain.models.PhotoComment
import us.mikeandwan.photos.domain.models.RANDOM_PREFERENCE_DEFAULT
import us.mikeandwan.photos.utils.ExifDataFormatter.prepareForDisplay
import us.mikeandwan.photos.ui.toImageGridItem
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor (
    private val photoCategoryRepository: PhotoCategoryRepository,
    private val fileRepository: FileStorageRepository,
    private val photoRepository: PhotoRepository,
    photoPreferenceRepository: PhotoPreferenceRepository
) : ViewModel() {
    private var slideshowJob: PeriodicJob<Unit>

    private val _showDetailSheet = MutableStateFlow(false)
    val showDetailSheet = _showDetailSheet.asStateFlow()

    private val slideshowDurationInMillis = photoPreferenceRepository
        .getSlideshowIntervalSeconds()
        .map { seconds -> (seconds * 1000).toLong() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, (RANDOM_PREFERENCE_DEFAULT.slideshowIntervalSeconds * 1000).toLong())

    var isSlideshowPlaying: StateFlow<Boolean>

    private val _category = MutableStateFlow<PhotoCategory?>(null)
    val category = _category.asStateFlow()

    private val _photos = MutableStateFlow<List<Photo>>(emptyList())
    val photos = _photos.asStateFlow()

    val gridItems = photos
        .map { photos -> photos.map { it.toImageGridItem() } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _activePhotoId = MutableStateFlow(-1)
    val activePhotoId = _activePhotoId.asStateFlow()

    val activePhotoIndex = photos.combine(activePhotoId) { photos, activePhotoId ->
        photos.indexOfFirst { it.id == activePhotoId }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, -1)

    val gridItemThumbnailSize = photoPreferenceRepository
        .getPhotoGridItemSize()
        .stateIn(viewModelScope, SharingStarted.Eagerly, GridThumbnailSize.Unspecified)

    fun loadCategory(categoryId: Int) {
        if(category.value?.id == categoryId) {
            return
        }

        viewModelScope.launch {
            photoCategoryRepository
                .getCategory(categoryId)
                .collect { _category.value = it }
        }
    }

    fun loadPhotos(categoryId: Int) {
        if(category.value?.id == categoryId) {
            return
        }

        viewModelScope.launch {
            photoCategoryRepository
                .getPhotos(categoryId)
                .filter { it is ExternalCallStatus.Success }
                .map { it as ExternalCallStatus.Success }
                .map { it.result }
                .collect { _photos.value = it }
        }

        viewModelScope.launch {
            isSlideshowPlaying
        }
    }

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
    private val _userRating = MutableStateFlow<Short>(0)
    val userRating = _userRating.asStateFlow()

    private val _averageRating = MutableStateFlow(0f)
    val averageRating = _averageRating.asStateFlow()

    fun setRating(rating: Short) {
        val newAverageRating = photoRepository.setRating(activePhotoId.value, rating)

        viewModelScope.launch {
            newAverageRating
                .filter { it is ExternalCallStatus.Success }
                .map { it as ExternalCallStatus.Success }
                .collect {
                    _userRating.value = rating
                    _averageRating.value = it.result.averageRating
                }
        }
    }

    fun fetchRatingDetails() {
        viewModelScope.launch {
            photoRepository.getRating(activePhotoId.value)
                .filter { it is ExternalCallStatus.Success }
                .map { it as ExternalCallStatus.Success }
                .collect {
                    _userRating.value = it.result.userRating
                    _averageRating.value = it.result.averageRating
                }
        }
    }

    // EXIF
    private val _exif = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val exif = _exif.asStateFlow()

    fun fetchExifDetails() {
        viewModelScope.launch {
            photoRepository.getExifData(activePhotoId.value)
                .filter { it is ExternalCallStatus.Success }
                .map { it as ExternalCallStatus.Success }
                .map { prepareForDisplay(it.result) }
                .collect { _exif.value = it }
        }
    }

    // COMMENTS
    private val _comments = MutableStateFlow(emptyList<PhotoComment>())
    val comments = _comments.asStateFlow()

    fun fetchCommentDetails() {
        viewModelScope.launch {
            photoRepository.getComments(activePhotoId.value)
                .map {
                    when (it) {
                        is ExternalCallStatus.Loading -> emptyList()
                        is ExternalCallStatus.Error -> emptyList()
                        is ExternalCallStatus.Success -> it.result
                    }
                }
                .collect { _comments.value = it }
        }
    }

    fun addComment(comment: String) {
        if(comment.isNotBlank()) {
            viewModelScope.launch {
                photoRepository.addComment(activePhotoId.value, comment)
                    .collect { result ->
                        if (result is ExternalCallStatus.Success) {
                            _comments.value = result.result
                        }
                    }
            }
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

package us.mikeandwan.photos.ui.screens.categoryItem

import android.graphics.drawable.Drawable
import androidx.lifecycle.viewModelScope
import androidx.media3.datasource.HttpDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import us.mikeandwan.photos.domain.models.MediaPreference
import us.mikeandwan.photos.domain.MediaCategoryRepository
import us.mikeandwan.photos.domain.MediaPreferenceRepository
import us.mikeandwan.photos.domain.guards.AuthGuard
import us.mikeandwan.photos.domain.guards.GuardStatus
import us.mikeandwan.photos.domain.models.MediaType
import us.mikeandwan.photos.domain.services.MediaListService
import us.mikeandwan.photos.ui.screens.category.BaseCategoryViewModel
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CategoryItemViewModel @Inject constructor (
    authGuard: AuthGuard,
    mediaCategoryRepository: MediaCategoryRepository,
    mediaPreferenceRepository: MediaPreferenceRepository,
    val videoPlayerDataSourceFactory: HttpDataSource.Factory,
    private val mediaListService: MediaListService
) : BaseCategoryViewModel(
    mediaCategoryRepository
) {
    // todo: consider restructuring to the stateholder pattern like in other VMs
    val activeMedia = mediaListService.activeMedia
    val activeId = mediaListService.activeId
    val activeIndex = mediaListService.activeIndex
    val isSlideshowPlaying = mediaListService.isSlideshowPlaying
    val showDetailSheet = mediaListService.showDetailSheet

    private var initialMediaId: Int = -1
    private var initialMediaUpdated = false

    fun setActiveIndex(index: Int) { mediaListService.setActiveIndex(index) }
    fun toggleSlideshow() { mediaListService.toggleSlideshow() }
    fun toggleShowDetails() { mediaListService.toggleShowDetails() }

    fun initState(categoryId: Int, mediaType: String, mediaId: Int) {
        val type = MediaType.valueOf(mediaType)

        loadCategory(type, categoryId)
        loadMedia(type, categoryId)

        initialMediaId = mediaId
    }

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

    private val slideshowDurationInMillis = mediaPreferenceRepository
        .getSlideshowIntervalSeconds()
        .map { seconds -> (seconds * 1000).toLong() }
        .stateIn(viewModelScope, WhileSubscribed(5000), (MediaPreference().slideshowIntervalSeconds * 1000).toLong())

    val isAuthorized = authGuard.status
        .map {
            when(it) {
                is GuardStatus.Failed -> false
                else -> true
            }
        }.stateIn(viewModelScope, WhileSubscribed(5000), true)

    init {
        mediaListService.initialize(
            media,
            slideshowDurationInMillis
        )

        viewModelScope.launch {
            media
                .filter { !initialMediaUpdated && initialMediaId > 0 }
                .filterNotNull()
                .filter { it.isNotEmpty() }
                .map {
                    initialMediaUpdated = true
                    mediaListService.setActiveId(initialMediaId)
                }
                .collect { }
        }
    }
}

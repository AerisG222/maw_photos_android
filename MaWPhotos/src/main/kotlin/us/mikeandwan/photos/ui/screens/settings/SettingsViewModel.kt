package us.mikeandwan.photos.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import us.mikeandwan.photos.authorization.AuthService
import us.mikeandwan.photos.domain.CategoryPreferenceRepository
import us.mikeandwan.photos.domain.ErrorRepository
import us.mikeandwan.photos.domain.NotificationPreferenceRepository
import us.mikeandwan.photos.domain.PhotoPreferenceRepository
import us.mikeandwan.photos.domain.RandomPreferenceRepository
import us.mikeandwan.photos.domain.SearchPreferenceRepository
import us.mikeandwan.photos.domain.models.CategoryDisplayType
import us.mikeandwan.photos.domain.models.GridThumbnailSize
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor (
    private val authService: AuthService,
    private val categoryPreferenceRepository: CategoryPreferenceRepository,
    private val notificationPreferenceRepository: NotificationPreferenceRepository,
    private val photoPreferenceRepository: PhotoPreferenceRepository,
    private val randomPreferenceRepository: RandomPreferenceRepository,
    private val searchPreferenceRepository: SearchPreferenceRepository,
    private val errorRepository: ErrorRepository
): ViewModel() {
    val notificationDoNotify = notificationPreferenceRepository
        .getDoNotify()
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val notificationDoVibrate = notificationPreferenceRepository
        .getDoVibrate()
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val categoryDisplayType = categoryPreferenceRepository
        .getCategoryDisplayType()
        .stateIn(viewModelScope, SharingStarted.Eagerly, CategoryDisplayType.Grid)

    val categoryThumbnailSize = categoryPreferenceRepository
        .getCategoryGridItemSize()
        .stateIn(viewModelScope, SharingStarted.Eagerly, GridThumbnailSize.Medium)

    val photoSlideshowInterval = photoPreferenceRepository
        .getSlideshowIntervalSeconds()
        .stateIn(viewModelScope, SharingStarted.Eagerly, 3)

    val photoThumbnailSize = photoPreferenceRepository
        .getPhotoGridItemSize()
        .stateIn(viewModelScope, SharingStarted.Eagerly, GridThumbnailSize.Medium)

    val randomSlideshowInterval = randomPreferenceRepository
        .getSlideshowIntervalSeconds()
        .stateIn(viewModelScope, SharingStarted.Eagerly, 3)

    val randomThumbnailSize = randomPreferenceRepository
        .getPhotoGridItemSize()
        .stateIn(viewModelScope, SharingStarted.Eagerly, GridThumbnailSize.Medium)

    val searchQueryCount = searchPreferenceRepository
        .getSearchesToSaveCount()
        .stateIn(viewModelScope, SharingStarted.Eagerly, 20)

    val searchDisplayType = searchPreferenceRepository
        .getSearchDisplayType()
        .stateIn(viewModelScope, SharingStarted.Eagerly, CategoryDisplayType.Grid)

    val searchThumbnailSize = searchPreferenceRepository
        .getSearchGridItemSize()
        .stateIn(viewModelScope, SharingStarted.Eagerly, GridThumbnailSize.Medium)

    fun setNotificationDoNotify(doNotify: Boolean) {
        viewModelScope.launch {
            notificationPreferenceRepository.setDoNotify(doNotify)
        }
    }

    fun setNotificationDoVibrate(doVibrate: Boolean) {
        viewModelScope.launch {
            notificationPreferenceRepository.setDoVibrate(doVibrate)
        }
    }

    fun setCategoryDisplayType(categoryDisplayType: CategoryDisplayType) {
        viewModelScope.launch {
            categoryPreferenceRepository.setCategoryDisplayType(categoryDisplayType)
        }
    }

    fun setCategoryThumbnailSize(categoryThumbnailSize: GridThumbnailSize) {
        viewModelScope.launch {
            categoryPreferenceRepository.setCategoryGridItemSize(categoryThumbnailSize)
        }
    }

    fun setPhotoSlideshowInterval(slideshowInterval: Int) {
        viewModelScope.launch {
            photoPreferenceRepository.setSlideshowIntervalSeconds(slideshowInterval)
        }
    }

    fun setPhotoThumbnailSize(photoThumbnailSize: GridThumbnailSize) {
        viewModelScope.launch {
            photoPreferenceRepository.setPhotoGridItemSize(photoThumbnailSize)
        }
    }

    fun setRandomSlideshowInterval(slideshowInterval: Int) {
        viewModelScope.launch {
            randomPreferenceRepository.setSlideshowIntervalSeconds(slideshowInterval)
        }
    }

    fun setRandomThumbnailSize(randomThumbnailSize: GridThumbnailSize) {
        viewModelScope.launch {
            randomPreferenceRepository.setPhotoGridItemSize(randomThumbnailSize)
        }
    }

    fun setSearchQueryCount(searchQueryCount: Int) {
        viewModelScope.launch {
            searchPreferenceRepository.setSearchesToSaveCount(searchQueryCount)
        }
    }

    fun setSearchDisplayType(searchDisplayType: CategoryDisplayType) {
        viewModelScope.launch {
            searchPreferenceRepository.setSearchDisplayType(searchDisplayType)
        }
    }

    fun setSearchThumbnailSize(searchThumbnailSize: GridThumbnailSize) {
        viewModelScope.launch {
            searchPreferenceRepository.setSearchGridItemSize(searchThumbnailSize)
        }
    }

    fun logout() {
        viewModelScope.launch {
            authService.logout()
        }
    }
}
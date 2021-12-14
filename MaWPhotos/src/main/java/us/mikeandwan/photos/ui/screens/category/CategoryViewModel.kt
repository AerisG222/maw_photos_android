package us.mikeandwan.photos.ui.screens.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import us.mikeandwan.photos.domain.ActiveIdRepository
import us.mikeandwan.photos.domain.PhotoCategoryRepository
import us.mikeandwan.photos.domain.PhotoPreferenceRepository
import us.mikeandwan.photos.domain.models.GridThumbnailSize
import us.mikeandwan.photos.domain.models.ExternalCallStatus
import us.mikeandwan.photos.ui.controls.imagegrid.ImageGridRecyclerAdapter
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor (
    private val activeIdRepository: ActiveIdRepository,
    private val photoCategoryRepository: PhotoCategoryRepository,
    photoPreferenceRepository: PhotoPreferenceRepository
) : ViewModel() {
    private val _requestNavigateToPhoto = MutableStateFlow<Int?>(null)
    val requestNavigateToPhoto = _requestNavigateToPhoto.asStateFlow()

    val photos = activeIdRepository
        .getActivePhotoCategoryId()
        .filter { it != null }
        .distinctUntilChanged()
        .flatMapLatest { photoCategoryRepository.getPhotos(it!!) }
        .filter { it is ExternalCallStatus.Success }
        .map { it as ExternalCallStatus.Success }
        .map { it.result }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val gridItemThumbnailSize = photoPreferenceRepository
        .getPhotoGridItemSize()
        .stateIn(viewModelScope, SharingStarted.Eagerly, GridThumbnailSize.Unspecified)

    val onPhotoClicked = ImageGridRecyclerAdapter.ClickListener {
        viewModelScope.launch {
            activeIdRepository.setActivePhoto(it.id)
            _requestNavigateToPhoto.value = it.id
        }
    }

    fun onNavigateComplete() {
        _requestNavigateToPhoto.value = null
    }
}
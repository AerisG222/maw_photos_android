package us.mikeandwan.photos.ui.screens.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import us.mikeandwan.photos.domain.ActiveIdRepository
import us.mikeandwan.photos.domain.PhotoCategoryRepository
import us.mikeandwan.photos.domain.PhotoPreferenceRepository
import us.mikeandwan.photos.domain.models.GridThumbnailSize
import us.mikeandwan.photos.domain.models.Photo
import us.mikeandwan.photos.ui.controls.imagegrid.ImageGridRecyclerAdapter
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CategoryViewModel @Inject constructor (
    private val activeIdRepository: ActiveIdRepository,
    private val photoCategoryRepository: PhotoCategoryRepository,
    private val photoPreferenceRepository: PhotoPreferenceRepository
) : ViewModel() {
    private val _requestNavigateToPhoto = MutableStateFlow<Int?>(null)
    val requestNavigateToPhoto = _requestNavigateToPhoto.asStateFlow()

    val photos = activeIdRepository
        .getActivePhotoCategoryId()
        .filter { it != null }
        .distinctUntilChanged()
        .flatMapLatest { photoCategoryRepository.getPhotos(it!!) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList<Photo>())

    val gridItemThumbnailSize = photoPreferenceRepository
        .getPhotoGridItemSize()
        .stateIn(viewModelScope, SharingStarted.Eagerly, GridThumbnailSize.Medium)

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
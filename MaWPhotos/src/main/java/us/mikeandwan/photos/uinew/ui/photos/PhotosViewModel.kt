package us.mikeandwan.photos.uinew.ui.photos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import us.mikeandwan.photos.domain.*
import us.mikeandwan.photos.uinew.ui.imagegrid.ImageGridItem
import us.mikeandwan.photos.uinew.ui.imagegrid.ImageGridRecyclerAdapter
import us.mikeandwan.photos.uinew.ui.photo.IPhotoListViewModel
import us.mikeandwan.photos.uinew.ui.toImageGridItem
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PhotosViewModel @Inject constructor (
    private val activeIdRepository: ActiveIdRepository,
    private val photoCategoryRepository: PhotoCategoryRepository,
    private val photoPreferenceRepository: PhotoPreferenceRepository
) : ViewModel(), IPhotoListViewModel {
    private val _activePhoto = MutableStateFlow<Photo?>(null)
    override val activePhoto = _activePhoto.asStateFlow()

    override val photoList = activeIdRepository
        .getActivePhotoCategoryId()
        .filter { it != null }
        .distinctUntilChanged()
        .flatMapLatest { photoCategoryRepository.getPhotos(it!!) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList<Photo>())

    val photos = photoList
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList<Photo>())

    val gridItemThumbnailSize = photoPreferenceRepository
        .getPhotoGridItemSize()
        .stateIn(viewModelScope, SharingStarted.Eagerly, GridThumbnailSize.Medium)

    val onPhotoClicked = ImageGridRecyclerAdapter.ClickListener {
        _activePhoto.value = it.data as Photo
    }
}
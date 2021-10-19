package us.mikeandwan.photos.uinew.ui.photos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import us.mikeandwan.photos.domain.ActiveIdRepository
import us.mikeandwan.photos.domain.PHOTO_PREFERENCE_DEFAULT
import us.mikeandwan.photos.domain.PhotoCategoryRepository
import us.mikeandwan.photos.domain.PhotoPreferenceRepository
import us.mikeandwan.photos.uinew.ui.imageGrid.ImageGridItem
import us.mikeandwan.photos.uinew.ui.toImageGridItem
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PhotosViewModel @Inject constructor (
    private val activeIdRepository: ActiveIdRepository,
    private val photoCategoryRepository: PhotoCategoryRepository,
    private val photoPreferenceRepository: PhotoPreferenceRepository
) : ViewModel() {
    val photos = activeIdRepository
        .getActivePhotoCategoryId()
        .filter { it != null }
        .flatMapLatest { photoCategoryRepository.getPhotos(it!!) }
        .map { list -> list.map { it.toImageGridItem() } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList<ImageGridItem>())

    val preferences = photoPreferenceRepository
        .getPhotoPreferences()
        .stateIn(viewModelScope, SharingStarted.Eagerly, PHOTO_PREFERENCE_DEFAULT)
}
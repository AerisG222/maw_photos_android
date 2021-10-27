package us.mikeandwan.photos.ui.controls.photoexif

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import us.mikeandwan.photos.domain.ActiveIdRepository
import us.mikeandwan.photos.domain.PhotoRepository
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class PhotoExifViewModel @Inject constructor (
    activeIdRepository: ActiveIdRepository,
    photoRepository: PhotoRepository
): ViewModel() {
    val exifData = activeIdRepository
        .getActivePhotoId()
        .filter { it != null }
        .flatMapLatest { photoRepository.getExifData(it!!) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)
}
package us.mikeandwan.photos.ui.screens.random

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import us.mikeandwan.photos.domain.RandomPhotoRepository
import us.mikeandwan.photos.domain.RandomPreferenceRepository
import us.mikeandwan.photos.domain.models.GridThumbnailSize
import javax.inject.Inject

@HiltViewModel
class RandomViewModel @Inject constructor(
    randomPhotoRepository: RandomPhotoRepository,
    randomPreferenceRepository: RandomPreferenceRepository
) : BaseRandomViewModel(
    randomPhotoRepository
) {
    val gridItemThumbnailSize = randomPreferenceRepository
        .getPhotoGridItemSize()
        .stateIn(viewModelScope, SharingStarted.Eagerly, GridThumbnailSize.Medium)
}

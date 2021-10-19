package us.mikeandwan.photos.uinew.ui.random

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import us.mikeandwan.photos.domain.RANDOM_PREFERENCE_DEFAULT
import us.mikeandwan.photos.domain.RandomPhotoRepository
import us.mikeandwan.photos.domain.RandomPreferenceRepository
import us.mikeandwan.photos.uinew.ui.imageGrid.ImageGridItem
import us.mikeandwan.photos.uinew.ui.toImageGridItem
import javax.inject.Inject

@HiltViewModel
class RandomViewModel @Inject constructor(
    private val randomPhotoRepository: RandomPhotoRepository,
    private val randomPreferenceRepository: RandomPreferenceRepository
) : ViewModel() {
    val photos = randomPhotoRepository
        .photos
        .map { photos -> photos.map { it.toImageGridItem() } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList<ImageGridItem>())

    val preferences = randomPreferenceRepository
        .getRandomPreferences()
        .stateIn(viewModelScope, SharingStarted.Eagerly, RANDOM_PREFERENCE_DEFAULT)

    suspend fun performInitialFetch() {
        randomPhotoRepository.fetch(20)
    }
}
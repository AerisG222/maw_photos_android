package us.mikeandwan.photos.uinew.ui.randomnavmenu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import us.mikeandwan.photos.domain.ActiveIdRepository
import us.mikeandwan.photos.domain.PhotoCategoryRepository
import us.mikeandwan.photos.domain.RandomPhotoRepository
import javax.inject.Inject

@HiltViewModel
class RandomMenuViewModel @Inject constructor (
    private val randomPhotoRepository: RandomPhotoRepository
): ViewModel() {
    fun fetch(count: Int) {
        viewModelScope.launch {
            randomPhotoRepository.fetch(count)
        }
    }

    fun clear() {
        randomPhotoRepository.clear()
    }
}
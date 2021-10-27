package us.mikeandwan.photos.ui.controls.randomnavmenu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import us.mikeandwan.photos.domain.NavigationStateRepository
import us.mikeandwan.photos.domain.RandomPhotoRepository
import javax.inject.Inject

@HiltViewModel
class RandomMenuViewModel @Inject constructor (
    private val randomPhotoRepository: RandomPhotoRepository,
    private val navigationStateRepository: NavigationStateRepository
): ViewModel() {
    fun fetch(count: Int) {
        viewModelScope.launch {
            randomPhotoRepository.fetch(count)
        }

        navigationStateRepository.requestNavDrawerClose()
    }

    fun clear() {
        randomPhotoRepository.clear()

        navigationStateRepository.requestNavDrawerClose()
    }
}
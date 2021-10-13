package us.mikeandwan.photos.uinew.ui.randomMenu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import us.mikeandwan.photos.domain.ActiveIdRepository
import us.mikeandwan.photos.domain.PhotoCategoryRepository
import javax.inject.Inject

@HiltViewModel
class RandomMenuViewModel @Inject constructor (
    private val photoCategoryRepository: PhotoCategoryRepository,
    private val activeIdRepository: ActiveIdRepository
): ViewModel() {
    fun fetch(count: Int) {
        viewModelScope.launch {
            throw NotImplementedError()
        }
    }

    fun clear() {
        throw NotImplementedError()
    }
}
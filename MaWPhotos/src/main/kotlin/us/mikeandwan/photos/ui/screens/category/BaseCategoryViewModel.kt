package us.mikeandwan.photos.ui.screens.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import us.mikeandwan.photos.BuildConfig
import us.mikeandwan.photos.domain.PhotoCategoryRepository
import us.mikeandwan.photos.domain.models.ExternalCallStatus
import us.mikeandwan.photos.domain.models.Photo
import us.mikeandwan.photos.domain.models.PhotoCategory

abstract class BaseCategoryViewModel (
    val photoCategoryRepository: PhotoCategoryRepository
) : ViewModel() {
    private val _category = MutableStateFlow<PhotoCategory?>(null)
    val category = _category.asStateFlow()

    private val _photos = MutableStateFlow<List<Photo>>(emptyList())
    val photos = _photos.asStateFlow()

    fun loadCategory(categoryId: Int) {
        if(category.value?.id == categoryId) {
            return
        }

        _category.value = null
        _photos.value = emptyList()

        viewModelScope.launch {
            if(BuildConfig.DEBUG) {
                delay(500)
            }

            photoCategoryRepository
                .getCategory(categoryId)
                .collect { _category.value = it }
        }
    }

    fun loadPhotos(categoryId: Int) {
        if(category.value?.id == categoryId) {
            return
        }

        viewModelScope.launch {
            if(BuildConfig.DEBUG) {
                delay(1000)
            }

            photoCategoryRepository
                .getPhotos(categoryId)
                .filter { it is ExternalCallStatus.Success }
                .map { it as ExternalCallStatus.Success }
                .map { it.result }
                .collect { _photos.value = it }
        }
    }
}

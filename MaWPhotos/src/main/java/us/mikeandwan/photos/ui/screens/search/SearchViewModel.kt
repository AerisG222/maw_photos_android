package us.mikeandwan.photos.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import us.mikeandwan.photos.domain.ActiveIdRepository
import us.mikeandwan.photos.domain.SearchPreferenceRepository
import us.mikeandwan.photos.domain.SearchRepository
import us.mikeandwan.photos.domain.models.CategoryDisplayType
import us.mikeandwan.photos.domain.models.GridThumbnailSize
import us.mikeandwan.photos.ui.controls.imagegrid.ImageGridRecyclerAdapter
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val activeIdRepository: ActiveIdRepository,
    private val searchRepository: SearchRepository,
    private val searchPreferenceRepository: SearchPreferenceRepository
) : ViewModel() {
    private val _requestNavigateToCategory = MutableStateFlow<Int?>(null)
    val requestNavigateToCategory = _requestNavigateToCategory.asStateFlow()

    val searchResults = searchRepository
        .searchResults
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val displayType = searchPreferenceRepository
        .getDisplayMode()
        .stateIn(viewModelScope, SharingStarted.Eagerly, CategoryDisplayType.Grid)

    val gridItemThumbnailSize = searchPreferenceRepository
        .getGridThumbnailSize()
        .stateIn(viewModelScope, SharingStarted.Eagerly, GridThumbnailSize.Medium)

    val onCategoryClicked = ImageGridRecyclerAdapter.ClickListener {
        viewModelScope.launch {
            activeIdRepository.setActivePhotoCategory(it.id)
            _requestNavigateToCategory.value = it.id
        }
    }

    fun onNavigateComplete() {
        _requestNavigateToCategory.value = null
    }

    fun search(query: String) {
        viewModelScope.launch {
            searchRepository.performSearch(query)
        }
    }
}
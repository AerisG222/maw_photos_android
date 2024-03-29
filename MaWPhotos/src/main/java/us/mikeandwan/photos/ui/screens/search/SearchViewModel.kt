package us.mikeandwan.photos.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import us.mikeandwan.photos.domain.ActiveIdRepository
import us.mikeandwan.photos.domain.SearchPreferenceRepository
import us.mikeandwan.photos.domain.SearchRepository
import us.mikeandwan.photos.domain.models.CategoryDisplayType
import us.mikeandwan.photos.domain.models.GridThumbnailSize
import us.mikeandwan.photos.ui.controls.categorychooser.CategoryChooserFragment
import us.mikeandwan.photos.ui.toDomainPhotoCategory
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val activeIdRepository: ActiveIdRepository,
    private val searchRepository: SearchRepository,
    val searchPreferenceRepository: SearchPreferenceRepository
) : ViewModel() {
    private val _requestNavigateToCategory = MutableStateFlow<Int?>(null)
    val requestNavigateToCategory = _requestNavigateToCategory.asStateFlow()

    val searchResults = searchRepository
        .searchResults
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val searchResultsAsCategories = searchResults
        .map { results -> results.map { it.toDomainPhotoCategory() }}
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val displayType = searchPreferenceRepository
        .getSearchDisplayType()
        .stateIn(viewModelScope, SharingStarted.Eagerly, CategoryDisplayType.Unspecified)

    val gridItemThumbnailSize = searchPreferenceRepository
        .getSearchGridItemSize()
        .stateIn(viewModelScope, SharingStarted.Eagerly, GridThumbnailSize.Medium)

    val moreResultsAvailable = combine(
            searchResults,
            searchRepository.totalFound
        ) { results, totalFound -> Pair(results, totalFound)}
        .map { (results, totalFound) -> results.size < totalFound }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val showNoResults = combine(
            searchRepository.searchRequest,
            searchRepository.searchResults,
            searchRepository.isSearching
        ) { request, results, isSearching -> Triple(request, results, isSearching)}
        .map { (request, results, isSearching) -> request.query.isNotEmpty() && results.isEmpty() && !isSearching }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val areResultsAvailable = combine(
            searchRepository.searchRequest,
            searchRepository.searchResults
        ) { request, results -> Pair(request, results) }
        .map { (request, results) -> request.query.isNotEmpty() && results.isNotEmpty() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val totalFound = searchRepository
        .totalFound
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val onCategoryClicked = CategoryChooserFragment.CategorySelectedListener {
        viewModelScope.launch {
            activeIdRepository.setActivePhotoCategory(it.id)
            _requestNavigateToCategory.value = it.id
        }
    }

    fun continueSearch() {
        viewModelScope.launch {
            searchRepository
                .continueSearch()
                .collect { }
        }
    }

    fun onNavigateComplete() {
        _requestNavigateToCategory.value = null
    }
}
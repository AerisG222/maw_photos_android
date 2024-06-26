package us.mikeandwan.photos.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.launch
import us.mikeandwan.photos.domain.SearchPreferenceRepository
import us.mikeandwan.photos.domain.SearchRepository
import us.mikeandwan.photos.domain.guards.AuthGuard
import us.mikeandwan.photos.domain.guards.GuardStatus
import us.mikeandwan.photos.domain.models.CategoryDisplayType
import us.mikeandwan.photos.domain.models.GridThumbnailSize
import us.mikeandwan.photos.domain.models.SearchSource
import us.mikeandwan.photos.ui.toDomainMediaCategory
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    authGuard: AuthGuard,
    private val searchRepository: SearchRepository,
    searchPreferenceRepository: SearchPreferenceRepository
) : ViewModel() {
    private val searchResults = searchRepository
        .searchResults
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val searchResultsAsCategories = searchResults
        .map { results -> results.map { it.toDomainMediaCategory() }}
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val displayType = searchPreferenceRepository
        .getSearchDisplayType()
        .stateIn(viewModelScope, SharingStarted.Eagerly, CategoryDisplayType.Unspecified)

    val gridItemThumbnailSize = searchPreferenceRepository
        .getSearchGridItemSize()
        .stateIn(viewModelScope, SharingStarted.Eagerly, GridThumbnailSize.Medium)

    val totalFound = searchRepository
        .totalFound
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val isAuthorized = authGuard.status
        .map {
            when(it) {
                is GuardStatus.Failed -> false
                else -> true
            }
        }.stateIn(viewModelScope, WhileSubscribed(5000), true)

    fun search(term: String) {
        viewModelScope.launch {
            searchRepository
                .performSearch(
                    query = term,
                    searchSource = SearchSource.SearchMenu
                )
                .collect { }
        }
    }

    fun continueSearch() {
        viewModelScope.launch {
            searchRepository
                .continueSearch()
                .collect { }
        }
    }
}

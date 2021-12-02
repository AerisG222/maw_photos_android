package us.mikeandwan.photos.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import us.mikeandwan.photos.api.PhotoApiClient
import us.mikeandwan.photos.database.SearchHistory
import us.mikeandwan.photos.database.SearchHistoryDao
import us.mikeandwan.photos.domain.models.SearchResultCategory
import java.util.*
import javax.inject.Inject

class SearchRepository @Inject constructor(
    private val api: PhotoApiClient,
    private val searchHistoryDao: SearchHistoryDao,
    private val searchPreferenceRepository: SearchPreferenceRepository
) {
    private val _searchResults = MutableStateFlow<List<SearchResultCategory>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    fun getSearchHistory() = searchHistoryDao
        .getSearchTerms()
        .map { history -> history.map { it.toDomainSearchHistory() }}

    suspend fun clearHistory() {
        searchHistoryDao.clearHistory()
    }

    suspend fun performSearch(query: String) {
        withContext(Dispatchers.IO) {
            addSearchHistory(query)

            val results = api.searchCategories(query, 0)

            _searchResults.value = results?.results?.map { it.toDomainSearchResult() } ?: emptyList()
        }
    }

    private suspend fun addSearchHistory(term: String) {
        searchHistoryDao.addSearchTerm(SearchHistory(
            term,
            Calendar.getInstance()
        ))
    }
}
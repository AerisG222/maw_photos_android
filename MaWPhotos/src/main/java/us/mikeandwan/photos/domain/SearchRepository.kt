package us.mikeandwan.photos.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import us.mikeandwan.photos.api.PhotoApiClient
import us.mikeandwan.photos.database.SearchHistory
import us.mikeandwan.photos.database.SearchHistoryDao
import us.mikeandwan.photos.domain.models.SearchRequest
import us.mikeandwan.photos.domain.models.SearchResultCategory
import us.mikeandwan.photos.domain.models.SearchSource
import java.util.*
import javax.inject.Inject
import kotlin.math.max

class SearchRepository @Inject constructor(
    private val api: PhotoApiClient,
    private val searchHistoryDao: SearchHistoryDao,
    private val searchPreferenceRepository: SearchPreferenceRepository
) {
    private val _searchRequest = MutableStateFlow(SearchRequest("", SearchSource.None))
    val searchRequest = _searchRequest.asStateFlow()

    private val _searchResults = MutableStateFlow<List<SearchResultCategory>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    private val _totalFound = MutableStateFlow(0)
    val totalFound = _totalFound.asStateFlow()

    fun getSearchHistory() = searchHistoryDao
        .getSearchTerms()
        .map { history -> history.map { it.toDomainSearchHistory() }}

    suspend fun clearHistory() {
        searchHistoryDao.clearHistory()
    }

    suspend fun performSearch(query: String, searchSource: SearchSource) {
        val currentQuery = searchRequest.value.query

        if(query.isBlank() || currentQuery.equals(query, true)) {
            return
        }

        _searchResults.value = emptyList()
        _searchRequest.value = SearchRequest(query, searchSource)

        withContext(Dispatchers.IO) {
            addSearchHistory(query)
            executeSearch(query, 0)
        }
    }

    suspend fun continueSearch() {
        val query = searchRequest.value.query
        val position = searchResults.value.size

        if(query.isBlank() || position < 0 || position >= totalFound.value) {
            return
        }

        executeSearch(query, position)
    }

    private suspend fun executeSearch(query: String, startPosition: Int) {
        val currentResults = searchResults.value

        withContext(Dispatchers.IO) {
            val results = api.searchCategories(query, startPosition)
            val domainResults = results?.results?.map { it.toDomainSearchResult() } ?: emptyList()

            _searchResults.value = currentResults + domainResults
            _totalFound.value = max(results?.totalFound ?: 0, _searchResults.value.size)
        }
    }

    private suspend fun addSearchHistory(term: String) {
        searchHistoryDao.addSearchTerm(SearchHistory(
            term,
            Calendar.getInstance()
        ))
    }
}
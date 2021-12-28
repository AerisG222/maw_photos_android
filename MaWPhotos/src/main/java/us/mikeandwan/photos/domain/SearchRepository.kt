package us.mikeandwan.photos.domain

import kotlinx.coroutines.flow.*
import timber.log.Timber
import us.mikeandwan.photos.api.ApiResult
import us.mikeandwan.photos.api.PhotoApiClient
import us.mikeandwan.photos.authorization.AuthService
import us.mikeandwan.photos.database.SearchHistory
import us.mikeandwan.photos.database.SearchHistoryDao
import us.mikeandwan.photos.domain.models.ExternalCallStatus
import us.mikeandwan.photos.domain.models.SearchRequest
import us.mikeandwan.photos.domain.models.SearchResultCategory
import us.mikeandwan.photos.domain.models.SearchSource
import us.mikeandwan.photos.ui.toExternalCallStatus
import java.util.*
import javax.inject.Inject
import kotlin.math.max

class SearchRepository @Inject constructor(
    private val api: PhotoApiClient,
    private val searchHistoryDao: SearchHistoryDao,
    private val searchPreferenceRepository: SearchPreferenceRepository,
    private val errorRepository: ErrorRepository,
    private val authService: AuthService
) {
    companion object {
        const val ERR_MSG_SEARCH = "Unable to search at this time.  Please try again later."
    }

    private val _searchRequest = MutableStateFlow(SearchRequest("", SearchSource.None))
    val searchRequest = _searchRequest.asStateFlow()

    private val _searchResults = MutableStateFlow<List<SearchResultCategory>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    private val _totalFound = MutableStateFlow(0)
    val totalFound = _totalFound.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    fun getSearchHistory() = searchHistoryDao
        .getSearchTerms()
        .map { history -> history.map { it.toDomainSearchHistory() }}

    suspend fun clearHistory() {
        searchHistoryDao.clearHistory()
    }

    fun performSearch(query: String, searchSource: SearchSource) = flow {
        val currentQuery = searchRequest.value.query

        _searchResults.value = emptyList()

        if(query.isNotBlank() && !currentQuery.equals(query, true)) {
            _searchRequest.value = SearchRequest(query, searchSource)
            _isSearching.value = true

            executeSearch(query, 0)
                .collect { emit(it) }

            if (searchResults.value.isNotEmpty()) {
                addSearchHistory(query)
            }

            _isSearching.value = false
        }
    }

    fun continueSearch() = flow {
        val query = searchRequest.value.query
        val position = searchResults.value.size

        if(query.isNotBlank() && position >= 0 && position <= totalFound.value) {
            executeSearch(query, position)
                .collect{ emit(it) }
        }
    }

    private fun executeSearch(query: String, startPosition: Int) = flow {
        val currentResults = searchResults.value

        emit(ExternalCallStatus.Loading)

        when(val result = api.searchCategories(query, startPosition)) {
            is ApiResult.Error -> {
                if(result.isUnauthorized()) {
                    authService.logout()
                } else {
                    errorRepository.showError(ERR_MSG_SEARCH)
                }

                emit(result.toExternalCallStatus())
            }
            is ApiResult.Empty -> {
                errorRepository.showError(ERR_MSG_SEARCH)
                emit(result.toExternalCallStatus())
            }
            is ApiResult.Success -> {
                val searchResults = result.result.results
                val domainResults = searchResults.map { it.toDomainSearchResult() }

                _searchResults.value = currentResults + domainResults
                _totalFound.value = max(result.result.totalFound, _searchResults.value.size)

                emit(domainResults)
            }
        }
    }

    private suspend fun addSearchHistory(term: String) {
        searchHistoryDao.addSearchTerm(SearchHistory(
            term,
            Calendar.getInstance()
        ))

        cleanSearchHistory()
    }

    private suspend fun cleanSearchHistory() {
        try {
            val historyToKeep = searchPreferenceRepository.getSearchesToSaveCount().first()
            val earliestDateToRemove = searchHistoryDao.getEarliestDateToRemove(historyToKeep)

            searchHistoryDao.removeOldHistory(earliestDateToRemove)
        } catch(t: Throwable) {
            Timber.e(t, "Error trying to clean up search history")
        }
    }
}
package us.mikeandwan.photos.api

import retrofit2.Retrofit
import javax.inject.Inject

class SearchApiClient @Inject constructor(
    retrofit: Retrofit
): BaseApiClient() {
    private val _searchApi: SearchApi by lazy { retrofit.create(SearchApi::class.java) }

    suspend fun searchCategories(query: String, start: Int = 0): ApiResult<SearchResults<SearchResultCategory>> {
        return makeApiCall(::searchCategories.name, suspend { _searchApi.searchCategories(query, start) })
    }
}

package us.mikeandwan.photos.api

import retrofit2.Response
import retrofit2.http.*

internal interface SearchApi {
    @GET("search/multimedia-categories")
    suspend fun searchCategories(@Query("query") query: String, @Query("start") start: Int): Response<SearchResults<SearchResultCategory>>
}

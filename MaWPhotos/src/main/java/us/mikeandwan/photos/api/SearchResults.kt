package us.mikeandwan.photos.api

import com.fasterxml.jackson.annotation.JsonProperty

class SearchResults<T> {
    @JsonProperty("results") var results: List<T> = ArrayList()
    @JsonProperty("totalFound") var totalFound: Int = 0
    @JsonProperty("startIndex") var startIndex: Int = 0
}
package us.mikeandwan.photos.api

import kotlinx.serialization.Serializable

@Serializable
class SearchResults<T> {
    var results: List<T> = ArrayList()
    var totalFound: Int = 0
    var startIndex: Int = 0
}
package us.mikeandwan.photos.domain.models

class SearchResults<T> {
    var results: List<T> = ArrayList()
    var totalFound: Int = 0
    var startIndex: Int = 0
}
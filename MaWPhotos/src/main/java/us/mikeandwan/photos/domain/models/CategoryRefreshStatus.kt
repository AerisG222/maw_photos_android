package us.mikeandwan.photos.domain.models

data class CategoryRefreshStatus (
    val isRefreshing: Boolean,
    val message: String?
)
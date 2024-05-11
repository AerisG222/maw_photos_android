package us.mikeandwan.photos.domain.models

data class CategoryRefreshStatus (
    val requestId: Int,
    val isRefreshing: Boolean,
    val message: String?
)

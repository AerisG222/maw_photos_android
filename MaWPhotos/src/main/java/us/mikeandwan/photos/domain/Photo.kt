package us.mikeandwan.photos.domain

data class Photo(
    val id: Int,
    val categoryId: Int,
    val mdHeight: Int,
    val mdWidth: Int,
    val mdUrl: String,
    val xsHeight: Int,
    val xsWidth: Int,
    val xsUrl: String
)

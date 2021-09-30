package us.mikeandwan.photos.domain

data class PhotoCategory(
    val id: Int,
    val year: Int,
    val name: String,
    val teaserHeight: Int,
    val teaserWidth: Int,
    val teaserUrl: String
)

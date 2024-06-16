package us.mikeandwan.photos.domain.models

data class MediaCategory(
    val type: MediaType,
    val id: Int,
    val year: Int,
    val name: String,
    val teaserHeight: Int,
    val teaserWidth: Int,
    val teaserUrl: String
)

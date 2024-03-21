package us.mikeandwan.photos.api

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: Int,
    val year: Int,
    val name: String,
    val teaserImage: MultimediaAsset
)
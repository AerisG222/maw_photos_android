package us.mikeandwan.photos.api

import kotlinx.serialization.Serializable

@Serializable
data class MultimediaAsset(
    val height: Int,
    val width: Int,
    val url: String,
    val size: Long
)

package us.mikeandwan.photos.api

import kotlinx.serialization.Serializable

@Serializable
data class Video(
    val id: Int,
    val categoryId: Int,
    val videoScaled: MultimediaAsset,
    val videoFull: MultimediaAsset,
    val thumbnail: MultimediaAsset
)

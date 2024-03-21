package us.mikeandwan.photos.api

import kotlinx.serialization.Serializable

@Serializable
data class Photo(
    val id: Int,
    val categoryId: Int,
    val imageXs: MultimediaAsset,
    val imageSm: MultimediaAsset,
    val imageMd: MultimediaAsset,
    val imageLg: MultimediaAsset
)
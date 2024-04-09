package us.mikeandwan.photos.api

import kotlinx.serialization.Serializable

@Serializable
data class RatePhoto(
    val photoId: Int,
    val rating: Short
)

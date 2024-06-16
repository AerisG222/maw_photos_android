package us.mikeandwan.photos.api

import kotlinx.serialization.Serializable

@Serializable
data class RateVideo(
    val videoId: Int,
    val rating: Short
)

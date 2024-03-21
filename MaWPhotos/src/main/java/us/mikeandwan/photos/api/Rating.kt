package us.mikeandwan.photos.api

import kotlinx.serialization.Serializable

@Serializable
data class Rating(
    val userRating: Short,
    val averageRating: Float
)

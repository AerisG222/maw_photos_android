package us.mikeandwan.photos.api

import com.fasterxml.jackson.annotation.JsonProperty

data class Rating(
    @JsonProperty("userRating") val userRating: Short,
    @JsonProperty("averageRating") val averageRating: Float
)

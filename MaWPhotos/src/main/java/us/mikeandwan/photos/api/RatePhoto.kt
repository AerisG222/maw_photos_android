package us.mikeandwan.photos.api

import com.fasterxml.jackson.annotation.JsonProperty

data class RatePhoto(
    @JsonProperty("photoId") val photoId: Int,
    @JsonProperty("rating") val rating: Short
)

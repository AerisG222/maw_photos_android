package us.mikeandwan.photos.api

import com.fasterxml.jackson.annotation.JsonProperty

data class MultimediaAsset(
    @JsonProperty("height") val height: Int,
    @JsonProperty("width") val width: Int,
    @JsonProperty("url") val url: String,
    @JsonProperty("size") val size: Long
)

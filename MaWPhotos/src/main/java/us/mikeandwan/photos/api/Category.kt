package us.mikeandwan.photos.api

import com.fasterxml.jackson.annotation.JsonProperty

data class Category(
    @JsonProperty("id") val id: Int,
    @JsonProperty("year") var year: Int,
    @JsonProperty("name") var name: String,
    @JsonProperty("teaserImage") var teaserImage: MultimediaAsset
)
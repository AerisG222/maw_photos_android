package us.mikeandwan.photos.api

import com.fasterxml.jackson.annotation.JsonProperty

data class Category(
    @JsonProperty("id") val id: Int,
    @JsonProperty("year") val year: Int,
    @JsonProperty("name") val name: String,
    @JsonProperty("teaserImage") val teaserImage: MultimediaAsset)
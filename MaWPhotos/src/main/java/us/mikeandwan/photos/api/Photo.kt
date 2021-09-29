package us.mikeandwan.photos.api

import com.fasterxml.jackson.annotation.JsonProperty

data class Photo(
    @JsonProperty("id") val id: Int,
    @JsonProperty("categoryId") val categoryId: Int,
    @JsonProperty("imageXs") val imageXs: MultimediaAsset,
    @JsonProperty("imageSm") val imageSm: MultimediaAsset,
    @JsonProperty("imageMd") val imageMd: MultimediaAsset,
    @JsonProperty("imageLg") val imageLg: MultimediaAsset
)
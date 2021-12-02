package us.mikeandwan.photos.api

import com.fasterxml.jackson.annotation.JsonProperty

data class SearchResultCategory(
    @JsonProperty("solrId") val solrId: String,
    @JsonProperty("id") val id: Int,
    @JsonProperty("year") val year: Int,
    @JsonProperty("name") val name: String,
    @JsonProperty("multimediaType") val multimediaType: String,
    @JsonProperty("teaserPhotoHeight") val teaserPhotoHeight: Int,
    @JsonProperty("teaserPhotoWidth") val teaserPhotoWidth: Int,
    @JsonProperty("teaserPhotoPath") val teaserPhotoPath: String,
    @JsonProperty("teaserPhotoSqHeight") val teaserPhotoSqHeight: Int,
    @JsonProperty("teaserPhotoSqWidth") val teaserPhotoSqWidth: Int,
    @JsonProperty("teaserPhotoSqPath") val teaserPhotoSqPath: String,
    @JsonProperty("score") val score: Double
)
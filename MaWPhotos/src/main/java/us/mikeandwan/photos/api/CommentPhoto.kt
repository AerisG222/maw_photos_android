package us.mikeandwan.photos.api

import com.fasterxml.jackson.annotation.JsonProperty

data class CommentPhoto(
    @JsonProperty("photoId") val photoId: Int,
    @JsonProperty("comment") val comment: String
)

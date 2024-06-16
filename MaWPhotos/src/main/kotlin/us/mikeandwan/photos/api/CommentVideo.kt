package us.mikeandwan.photos.api

import kotlinx.serialization.Serializable

@Serializable
data class CommentVideo(
    val videoId: Int,
    val comment: String
)

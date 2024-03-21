package us.mikeandwan.photos.api

import kotlinx.serialization.Serializable

@Serializable
data class CommentPhoto(
    val photoId: Int,
    val comment: String
)

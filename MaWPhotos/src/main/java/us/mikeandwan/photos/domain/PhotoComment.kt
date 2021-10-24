package us.mikeandwan.photos.domain

import java.util.*

data class PhotoComment(
    val entryDate: Date,
    val commentText: String,
    val username: String
)

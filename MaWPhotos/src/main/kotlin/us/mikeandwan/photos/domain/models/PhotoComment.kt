package us.mikeandwan.photos.domain.models

import java.util.*

data class PhotoComment(
    val entryDate: Date,
    val commentText: String,
    val username: String
)

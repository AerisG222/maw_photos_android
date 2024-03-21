package us.mikeandwan.photos.api

import kotlinx.serialization.Contextual
import java.util.*
import kotlinx.serialization.Serializable

@Serializable
data class Comment(
    @Contextual val entryDate: Date,
    val commentText: String,
    val username: String
)

package us.mikeandwan.photos.api

import java.util.*
import kotlinx.serialization.Serializable
import us.mikeandwan.photos.api.serializers.DateSerializer

@Serializable
data class Comment(
    @Serializable(with = DateSerializer::class)
    val entryDate: Date,
    val commentText: String,
    val username: String
)

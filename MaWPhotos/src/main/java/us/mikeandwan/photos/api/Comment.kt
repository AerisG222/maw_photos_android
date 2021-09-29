package us.mikeandwan.photos.api

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class Comment(
    @JsonProperty("entryDate") val entryDate: Date,
    @JsonProperty("commentText") val commentText: String,
    @JsonProperty("username") val username: String
)

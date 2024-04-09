package us.mikeandwan.photos.api

import kotlinx.serialization.Serializable

@Serializable
data class FileLocation (
    val username: String,
    val filename: String,
    val relativePath: String,
)

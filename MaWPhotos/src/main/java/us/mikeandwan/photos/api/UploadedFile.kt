package us.mikeandwan.photos.api

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class UploadedFile(
    val location: FileLocation?,
    @Contextual val creationTime: Date?,
    val sizeInBytes: Long
)

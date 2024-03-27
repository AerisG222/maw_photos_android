package us.mikeandwan.photos.api

import kotlinx.serialization.Serializable
import us.mikeandwan.photos.api.serializers.DateSerializer
import java.util.*

@Serializable
data class UploadedFile(
    val location: FileLocation?,
    @Serializable(with = DateSerializer::class)
    val creationTime: Date?,
    val sizeInBytes: Long
)

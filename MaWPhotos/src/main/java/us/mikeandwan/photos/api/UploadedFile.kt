package us.mikeandwan.photos.api

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class UploadedFile(
    @JsonProperty("location") val location: FileLocation?,
    @get:JsonFormat(
        shape = JsonFormat.Shape.STRING,
        pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS",
        timezone = "EST"
    )
    @JsonProperty("creationTime") val creationTime: Date?,
    @JsonProperty("sizeInBytes") val sizeInBytes: Long
)

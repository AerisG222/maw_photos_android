package us.mikeandwan.photos.models

import com.fasterxml.jackson.annotation.JsonFormat
import java.util.*

class UploadedFile {
    var location: FileLocation? = null

    @get:JsonFormat(
        shape = JsonFormat.Shape.STRING,
        pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS",
        timezone = "EST"
    )
    var creationTime: Date? = null
    var sizeInBytes: Long = 0
}
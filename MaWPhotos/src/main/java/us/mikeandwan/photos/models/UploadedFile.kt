package us.mikeandwan.photos.models

import us.mikeandwan.photos.models.MultimediaAsset
import okhttp3.ResponseBody
import us.mikeandwan.photos.models.FileLocation
import com.fasterxml.jackson.annotation.JsonFormat
import us.mikeandwan.photos.models.UploadedFile
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
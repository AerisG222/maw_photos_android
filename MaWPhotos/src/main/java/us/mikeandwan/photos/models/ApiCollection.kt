package us.mikeandwan.photos.models

import us.mikeandwan.photos.models.MultimediaAsset
import okhttp3.ResponseBody
import us.mikeandwan.photos.models.FileLocation
import com.fasterxml.jackson.annotation.JsonFormat
import us.mikeandwan.photos.models.UploadedFile
import java.util.ArrayList

class ApiCollection<T> {
    var count: Long = 0
    var items: List<T> = ArrayList()
}
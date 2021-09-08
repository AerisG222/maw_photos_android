package us.mikeandwan.photos.models

import us.mikeandwan.photos.models.MultimediaAsset
import okhttp3.ResponseBody
import us.mikeandwan.photos.models.FileLocation
import com.fasterxml.jackson.annotation.JsonFormat
import us.mikeandwan.photos.models.UploadedFile

class Category {
    var id = 0
    var year = 0
    lateinit var name: String
    lateinit var teaserImage: MultimediaAsset

    override fun equals(`object`: Any?): Boolean {
        if (`object` == null) {
            return false
        }
        if (this === `object`) {
            return true
        }
        return if (`object` is Category) {
            id == `object`.id
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        return id
    }
}
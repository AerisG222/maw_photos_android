package us.mikeandwan.photos.models

import java.io.Serializable

class Photo : Serializable {
    var id = 0
    var categoryId = 0
    lateinit var imageXs: MultimediaAsset
    lateinit var imageSm: MultimediaAsset
    lateinit var imageMd: MultimediaAsset
    lateinit var imageLg: MultimediaAsset

    override fun equals(`object`: Any?): Boolean {
        if (`object` == null) {
            return false
        }
        if (this === `object`) {
            return true
        }
        return if (`object` is Photo) {
            id == `object`.id
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        return id
    }

    companion object {
        private const val serialVersionUID: Long = 1
    }
}
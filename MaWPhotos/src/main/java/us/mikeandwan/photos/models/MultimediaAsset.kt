package us.mikeandwan.photos.models

import java.io.Serializable

class MultimediaAsset : Serializable {
    var height = 0
    var width = 0
    lateinit var url: String
    var size: Long = 0

    companion object {
        private const val serialVersionUID: Long = 1
    }
}
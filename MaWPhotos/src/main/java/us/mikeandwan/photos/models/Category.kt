package us.mikeandwan.photos.models

class Category {
    var id = 0
    var year = 0
    lateinit var name: String
    lateinit var teaserImage: MultimediaAsset

    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }
        if (this === other) {
            return true
        }
        return if (other is Category) {
            id == other.id
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        return id
    }
}
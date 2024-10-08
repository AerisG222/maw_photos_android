package us.mikeandwan.photos.domain.models

data class Photo (
    override var type: MediaType,
    override var id: Int,
    override var categoryId: Int,
    override var thumbnailHeight: Int,
    override var thumbnailWidth: Int,
    override var thumbnailUrl: String,
    val mdHeight: Int,
    val mdWidth: Int,
    val mdUrl: String,
) : Media()

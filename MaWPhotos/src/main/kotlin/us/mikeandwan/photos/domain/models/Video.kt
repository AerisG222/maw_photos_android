package us.mikeandwan.photos.domain.models

data class Video (
    override var type: MediaType,
    override var id: Int,
    override var categoryId: Int,
    override var thumbnailHeight: Int,
    override var thumbnailWidth: Int,
    override var thumbnailUrl: String,
    val scaledHeight: Int,
    val scaledWidth: Int,
    val scaledUrl: String,
) : Media()

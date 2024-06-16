package us.mikeandwan.photos.domain.models

abstract class Media {
    abstract var type: MediaType
    abstract var id: Int
    abstract var categoryId: Int
    abstract var thumbnailHeight: Int
    abstract var thumbnailWidth: Int
    abstract var thumbnailUrl: String
}

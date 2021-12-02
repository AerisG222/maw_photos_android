package us.mikeandwan.photos.domain.models

data class SearchResultCategory(
    val solrId: String,
    val id: Int,
    val year: Int,
    val name: String,
    val multimediaType: String,
    val teaserPhotoHeight: Int,
    val teaserPhotoWidth: Int,
    val teaserPhotoPath: String,
    val teaserPhotoSqHeight: Int,
    val teaserPhotoSqWidth: Int,
    val teaserPhotoSqPath: String,
    val teaserPhotoMdPath: String,
    val score: Double
)
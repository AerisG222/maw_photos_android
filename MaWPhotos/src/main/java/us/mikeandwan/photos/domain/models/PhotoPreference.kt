package us.mikeandwan.photos.domain.models

data class PhotoPreference (
    val slideshowIntervalSeconds: Int,
    val gridThumbnailSize: GridThumbnailSize
)

val PHOTO_PREFERENCE_DEFAULT = PhotoPreference(
    3,
    GridThumbnailSize.Medium
)

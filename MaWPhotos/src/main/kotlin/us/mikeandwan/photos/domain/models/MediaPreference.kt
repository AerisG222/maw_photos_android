package us.mikeandwan.photos.domain.models

data class MediaPreference (
    val slideshowIntervalSeconds: Int,
    val gridThumbnailSize: GridThumbnailSize
)

val MEDIA_PREFERENCE_DEFAULT = MediaPreference(
    3,
    GridThumbnailSize.Unspecified
)

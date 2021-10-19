package us.mikeandwan.photos.domain

data class RandomPreference (
    val slideshowIntervalSeconds: Int,
    val gridThumbnailSize: GridThumbnailSize
)

val RANDOM_PREFERENCE_DEFAULT = RandomPreference(
    3,
    GridThumbnailSize.Medium
)

package us.mikeandwan.photos.domain

data class PhotoPreference (
    val displayToolbar: Boolean,
    val displayThumbnails: Boolean,
    val displayTopToolbar: Boolean,
    val doFadeControls: Boolean,
    val slideshowIntervalSeconds: Int,
    val gridThumbnailSize: GridThumbnailSize
)

val PHOTO_PREFERENCE_DEFAULT = PhotoPreference(
    true,
    true,
    true,
    true,
    3,
    GridThumbnailSize.Medium
)

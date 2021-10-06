package us.mikeandwan.photos.domain

data class PhotoPreference (
    val displayToolbar: Boolean,
    val displayThumbnails: Boolean,
    val displayTopToolbar: Boolean,
    val doFadeControls: Boolean,
    val slideshowIntervalSeconds: Int
)

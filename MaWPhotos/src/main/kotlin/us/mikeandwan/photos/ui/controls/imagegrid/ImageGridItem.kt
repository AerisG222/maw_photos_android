package us.mikeandwan.photos.ui.controls.imagegrid

data class ImageGridItem<T> (
    val id: Int,
    val url: String,
    val data: T
)

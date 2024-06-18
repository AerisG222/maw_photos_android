package us.mikeandwan.photos.ui.controls.imagegrid

import androidx.compose.runtime.Composable
import us.mikeandwan.photos.domain.models.GridThumbnailSize

data class ImageGridState<T> (
    val gridItems: List<ImageGridItem<T>>,
    val thumbnailSize: GridThumbnailSize,
    val onSelectGridItem: (ImageGridItem<T>) -> Unit
)

@Composable
fun <T> rememberImageGridState(
    gridItems: List<ImageGridItem<T>> = emptyList(),
    thumbnailSize: GridThumbnailSize = GridThumbnailSize.Unspecified,
    onSelectGridItem: (ImageGridItem<T>) -> Unit = {}
): ImageGridState<T> {
    return ImageGridState(
        gridItems,
        thumbnailSize,
        onSelectGridItem
    )
}

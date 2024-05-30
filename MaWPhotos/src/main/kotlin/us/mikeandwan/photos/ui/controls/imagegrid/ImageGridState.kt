package us.mikeandwan.photos.ui.controls.imagegrid

import androidx.compose.runtime.Composable
import us.mikeandwan.photos.domain.models.GridThumbnailSize

data class ImageGridState (
    val gridItems: List<ImageGridItem>,
    val thumbnailSize: GridThumbnailSize,
    val onSelectGridItem: (ImageGridItem) -> Unit
)

@Composable
fun rememberImageGridState(
    gridItems: List<ImageGridItem> = emptyList(),
    thumbnailSize: GridThumbnailSize = GridThumbnailSize.Unspecified,
    onSelectGridItem: (ImageGridItem) -> Unit = {}
): ImageGridState {
    return ImageGridState(
        gridItems,
        thumbnailSize,
        onSelectGridItem
    )
}

package us.mikeandwan.photos.ui.controls.imagegrid

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import us.mikeandwan.photos.R
import us.mikeandwan.photos.domain.models.GridThumbnailSize

@Composable
fun ImageGrid(state: ImageGridState) {
    val size = getSize(state.thumbnailSize)

    if(size > 0.dp) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = size),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            items(state.gridItems) {
                ImageGridImage(
                    item = it,
                    size = size,
                    onSelectImage = { item -> state.onSelectGridItem(item) }
                )
            }
        }
    }
}

@Composable
fun getSize(size: GridThumbnailSize): Dp {
    return when (size) {
        GridThumbnailSize.ExtraSmall -> 60.dp
        GridThumbnailSize.Small -> 90.dp
        GridThumbnailSize.Medium -> 120.dp
        GridThumbnailSize.Large -> 180.dp
        GridThumbnailSize.Unspecified -> 0.dp
    }
}

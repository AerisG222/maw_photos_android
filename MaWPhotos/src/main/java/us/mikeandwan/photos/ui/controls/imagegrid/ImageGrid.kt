package us.mikeandwan.photos.ui.controls.imagegrid

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import us.mikeandwan.photos.R
import us.mikeandwan.photos.domain.models.GridThumbnailSize
import us.mikeandwan.photos.ui.ImageGridClickListener

@Composable
fun ImageGrid(
    viewModel: ImageGridViewModel,
    clickListener: ImageGridClickListener?
) {
    val gridItems = viewModel.gridItems.collectAsState()
    val size = viewModel.thumbnailSize.collectAsState()

    @Composable
    fun getSize(size: GridThumbnailSize): Dp {
        return when (size) {
            GridThumbnailSize.ExtraSmall -> dimensionResource(R.dimen.image_grid_thumbnail_size_extra_small)
            GridThumbnailSize.Small -> dimensionResource(R.dimen.image_grid_thumbnail_size_small)
            GridThumbnailSize.Medium -> dimensionResource(R.dimen.image_grid_thumbnail_size_medium)
            GridThumbnailSize.Large -> dimensionResource(R.dimen.image_grid_thumbnail_size_large)
            GridThumbnailSize.Unspecified -> 0.dp
        }
    }

    if(getSize(size.value) > 0.dp) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = getSize(size.value)),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            items(gridItems.value) {
                ImageGridImage(item = it, size = getSize(size.value), onSelectImage = clickListener)
            }
        }
    }
}

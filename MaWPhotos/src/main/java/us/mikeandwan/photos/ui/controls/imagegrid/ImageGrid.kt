package us.mikeandwan.photos.ui.controls.imagegrid

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.unit.dp
import us.mikeandwan.photos.ui.ImageGridClickListener

@Composable
fun ImageGrid(
    viewModel: ImageGridViewModel,
    clickListener: ImageGridClickListener?
) {
    val gridItems = viewModel.gridItemsWithSize.collectAsState()

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 128.dp)
    ) {
        items(gridItems.value) {
            ImageGridImage(item = it, onSelectImage = clickListener)
        }
    }
}

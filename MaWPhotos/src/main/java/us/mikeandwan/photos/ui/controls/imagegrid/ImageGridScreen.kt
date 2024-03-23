package us.mikeandwan.photos.ui.controls.imagegrid

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState

@Composable
fun ImageGridScreen(
    viewModel: ImageGridViewModel,
    onSelectGridItem: (item: ImageGridItem) -> Unit
) {
    val items = viewModel.gridItems.collectAsState()
    val size = viewModel.thumbnailSize.collectAsState()

    ImageGrid(
        items.value,
        size.value,
        onSelectGridItem
    )
}

package us.mikeandwan.photos.ui.screens.category

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import us.mikeandwan.photos.ui.controls.imagegrid.ImageGridScreen
import us.mikeandwan.photos.ui.controls.imagegrid.ImageGridViewModel
import us.mikeandwan.photos.ui.theme.AppTheme
import us.mikeandwan.photos.ui.toImageGridItem

@Composable
fun CategoryScreen(
    viewModel: CategoryViewModel
) {
    val imageGridViewModel = ImageGridViewModel()
    val photos = viewModel.photos.collectAsState()
    val thumbSize = viewModel.gridItemThumbnailSize.collectAsState()

    imageGridViewModel.setGridItems(photos.value.map { it.toImageGridItem() })
    imageGridViewModel.setThumbnailSize(thumbSize.value)

    AppTheme {
        ImageGridScreen(imageGridViewModel, viewModel.onPhotoClicked)
    }
}

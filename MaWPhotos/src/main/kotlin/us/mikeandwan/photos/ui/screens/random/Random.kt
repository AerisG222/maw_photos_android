package us.mikeandwan.photos.ui.screens.random

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import us.mikeandwan.photos.domain.models.GridThumbnailSize
import us.mikeandwan.photos.domain.models.Photo
import us.mikeandwan.photos.ui.controls.imagegrid.ImageGrid
import us.mikeandwan.photos.ui.controls.imagegrid.ImageGridItem
import us.mikeandwan.photos.ui.theme.AppTheme
import us.mikeandwan.photos.ui.toImageGridItem

const val RandomRoute = "random"

fun NavGraphBuilder.randomScreen(
    onNavigateToPhoto: (Int) -> Unit
) {
    composable(RandomRoute) {
        val vm: RandomViewModel = hiltViewModel()

        val photos by vm.photos.collectAsStateWithLifecycle()
        val thumbSize by vm.gridItemThumbnailSize.collectAsStateWithLifecycle()

        RandomScreen(
            photos,
            thumbSize,
            onPhotoClicked = { onNavigateToPhoto(it.id) }
        )
    }
}

fun NavController.navigateToRandom() {
    this.navigate(RandomRoute)
}

@Composable
fun RandomScreen(
    photos: List<Photo>,
    thumbSize: GridThumbnailSize,
    onPhotoClicked: (ImageGridItem) -> Unit
) {
    val gridItems = photos.map { it.toImageGridItem() }

    AppTheme {
        ImageGrid(
            gridItems,
            thumbSize,
            onSelectGridItem = onPhotoClicked
        )
    }
}


//override fun onResume() {
//    viewModel.onResume()
//
//    super.onResume()
//}
//
//override fun onPause() {
//    viewModel.onPause()
//
//    super.onPause()
//}
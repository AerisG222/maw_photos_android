package us.mikeandwan.photos.ui.screens.category

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

const val CategoryRoute = "category"
private const val categoryIdArg = "categoryId"

fun NavGraphBuilder.categoryScreen(
    onNavigateToPhoto: (Int) -> Unit
) {
    composable("$CategoryRoute/{$categoryIdArg}") {
        val vm: CategoryViewModel = hiltViewModel()

        val photos by vm.photos.collectAsStateWithLifecycle()
        val thumbSize by vm.gridItemThumbnailSize.collectAsStateWithLifecycle()

        CategoryScreen(
            photos,
            thumbSize,
            onPhotoClicked = { onNavigateToPhoto(it.id) }
        )
    }
}

fun NavController.navigateToCategory(categoryId: Int) {
    this.navigate("$CategoryRoute/$categoryId")
}

@Composable
fun CategoryScreen(
    photos: List<Photo>,
    thumbSize: GridThumbnailSize,
    onPhotoClicked: (ImageGridItem) -> Unit
) {
    val gridItems = photos.map { it.toImageGridItem() }

    AppTheme {
        ImageGrid(
            gridItems = gridItems,
            thumbnailSize = thumbSize,
            onSelectGridItem = onPhotoClicked
        )
    }
}

package us.mikeandwan.photos.ui.screens.category

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import us.mikeandwan.photos.domain.models.GridThumbnailSize
import us.mikeandwan.photos.domain.models.PhotoCategory
import us.mikeandwan.photos.ui.controls.imagegrid.ImageGrid
import us.mikeandwan.photos.ui.controls.imagegrid.ImageGridItem

const val CategoryRoute = "category"
private const val categoryIdArg = "categoryId"

fun buildTitle(category: PhotoCategory?): String {
    return when(category) {
        null -> ""
        else -> category.name
    }
}

fun NavGraphBuilder.categoryScreen(
    onNavigateToPhoto: (Int) -> Unit,
    updateTopBar : (Boolean, Boolean, String) -> Unit
) {
    composable(
        route = "$CategoryRoute/{$categoryIdArg}",
        arguments = listOf(
            navArgument(categoryIdArg) { type = NavType.IntType }
        )
    ) { backStackEntry ->
        val vm: CategoryViewModel = hiltViewModel()
        val categoryId = backStackEntry.arguments?.getInt(categoryIdArg) ?: 0
        vm.loadCategory(categoryId)
        vm.loadPhotos(categoryId)

        val category by vm.category.collectAsStateWithLifecycle()
        val photos by vm.photos.collectAsStateWithLifecycle()
        val thumbSize by vm.gridItemThumbnailSize.collectAsStateWithLifecycle()

        updateTopBar(true, true, buildTitle(category))

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
    photos: List<ImageGridItem>,
    thumbSize: GridThumbnailSize,
    onPhotoClicked: (ImageGridItem) -> Unit
) {
    ImageGrid(
        gridItems = photos,
        thumbnailSize = thumbSize,
        onSelectGridItem = onPhotoClicked
    )
}

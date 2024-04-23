package us.mikeandwan.photos.ui.screens.category

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import us.mikeandwan.photos.domain.models.GridThumbnailSize
import us.mikeandwan.photos.domain.models.NavigationArea
import us.mikeandwan.photos.domain.models.Photo
import us.mikeandwan.photos.domain.models.PhotoCategory
import us.mikeandwan.photos.ui.controls.imagegrid.ImageGrid
import us.mikeandwan.photos.ui.controls.imagegrid.ImageGridItem
import us.mikeandwan.photos.ui.controls.photopager.PhotoPager

const val CategoryRoute = "category"
private const val categoryIdArg = "categoryId"
private const val photoIdArg = "photoId"

fun buildTitle(category: PhotoCategory?): String {
    return when(category) {
        null -> ""
        else -> category.name
    }
}

fun NavGraphBuilder.categoryScreen(
    updateTopBar : (Boolean, Boolean, String) -> Unit,
    setNavArea: (NavigationArea) -> Unit
) {
    composable(
        route = "$CategoryRoute/{$categoryIdArg}",
        arguments = listOf(
            navArgument(categoryIdArg) { type = NavType.IntType },
            navArgument(photoIdArg) { type = NavType.IntType; defaultValue = -1 }
        )
    ) { backStackEntry ->
        val vm: CategoryViewModel = hiltViewModel()
        val categoryId = backStackEntry.arguments?.getInt(categoryIdArg) ?: -1

        LaunchedEffect(categoryId) {
            vm.loadCategory(categoryId)
            vm.loadPhotos(categoryId)
        }

        val category by vm.category.collectAsStateWithLifecycle()
        val gridItems by vm.gridItems.collectAsStateWithLifecycle()
        val thumbSize by vm.gridItemThumbnailSize.collectAsStateWithLifecycle()
        val photos by vm.photos.collectAsStateWithLifecycle()
        val activePhotoId by vm.activePhotoId.collectAsStateWithLifecycle()
        val activePhotoIndex by vm.activePhotoIndex.collectAsStateWithLifecycle()

        updateTopBar(true, true, buildTitle(category))
        setNavArea(NavigationArea.Category)

        if(category != null) {
            CategoryScreen(
                category!!,
                gridItems,
                thumbSize,
                photos,
                activePhotoId,
                activePhotoIndex,
                onPhotoClicked = { vm.setActivePhotoId(it.id) }
            )
        }
    }
}

fun NavController.navigateToCategory(categoryId: Int) {
    this.navigate("$CategoryRoute/$categoryId")
}

@Composable
fun CategoryScreen(
    category: PhotoCategory,
    gridItems: List<ImageGridItem>,
    thumbSize: GridThumbnailSize,
    photos: List<Photo>,
    activePhotoId: Int,
    activePhotoIndex: Int,
    onPhotoClicked: (ImageGridItem) -> Unit
) {
    if(activePhotoId <= 0) {
        ImageGrid(
            gridItems = gridItems,
            thumbnailSize = thumbSize,
            onSelectGridItem = onPhotoClicked
        )
    } else {
        PhotoPager(
            activePhotoIndex = activePhotoIndex,
            category = category,
            photos = photos,
            showPositionAndCount = true,
            showYearAndCategory = false,
            isSlideshowPlaying = false,
            showDetails = false,
            navigateToYear = { },
            navigateToCategory = { },
            toggleSlideshow = { },
            sharePhoto = { },
            toggleDetails = { }
        )
    }
}

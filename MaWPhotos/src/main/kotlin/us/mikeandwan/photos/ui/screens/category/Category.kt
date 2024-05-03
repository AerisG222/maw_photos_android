package us.mikeandwan.photos.ui.screens.category

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    setNavArea: (NavigationArea) -> Unit,
    navigateToPhoto: (categoryId: Int, photoId: Int) -> Unit
) {
    composable(
        route = "$CategoryRoute/{$categoryIdArg}?photoId={$photoIdArg}",
        arguments = listOf(
            navArgument(categoryIdArg) { type = NavType.IntType },
            navArgument(photoIdArg) { type = NavType.IntType; defaultValue = -1 }
        )
    ) { backStackEntry ->
        val vm: CategoryViewModel = hiltViewModel()
        val categoryId = backStackEntry.arguments?.getInt(categoryIdArg) ?: -1
        val photoId = backStackEntry.arguments?.getInt(photoIdArg) ?: -1

        LaunchedEffect(categoryId) {
            vm.loadCategory(categoryId)
            vm.loadPhotos(categoryId)

            setNavArea(NavigationArea.Category)
        }

        LaunchedEffect(photoId) {
            if(photoId > 0) {
                vm.setActivePhotoId(photoId)
            }
        }

        val category by vm.category.collectAsStateWithLifecycle()
        val gridItems by vm.gridItems.collectAsStateWithLifecycle()
        val thumbSize by vm.gridItemThumbnailSize.collectAsStateWithLifecycle()
        val photos by vm.photos.collectAsStateWithLifecycle()
        val activePhotoId by vm.activePhotoId.collectAsStateWithLifecycle()
        val activePhotoIndex by vm.activePhotoIndex.collectAsStateWithLifecycle()
        val isSlideshowPlaying by vm.isSlideshowPlaying.collectAsStateWithLifecycle()

        LaunchedEffect(category) {
            updateTopBar(true, true, buildTitle(category))
        }

        if(category != null) {
            CategoryScreen(
                category!!,
                gridItems,
                thumbSize,
                photos,
                activePhotoId,
                activePhotoIndex,
                isSlideshowPlaying,
                navigateToPhoto = navigateToPhoto,
                updateActivePhoto = { newPhotoId -> vm.setActivePhotoId(newPhotoId) },
                toggleSlideshow = { vm.toggleSlideshow() }
            )
        }
    }
}

fun NavController.navigateToCategory(categoryId: Int, photoId: Int? = null) {
    if(photoId == null) {
        this.navigate("$CategoryRoute/$categoryId")
    } else {
        this.navigate("$CategoryRoute/$categoryId?$photoIdArg=$photoId")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    category: PhotoCategory,
    gridItems: List<ImageGridItem>,
    thumbSize: GridThumbnailSize,
    photos: List<Photo>,
    activePhotoId: Int,
    activePhotoIndex: Int,
    isSlideshowPlaying: Boolean,
    navigateToPhoto: (categoryId: Int, photoId: Int) -> Unit,
    updateActivePhoto: (photoId: Int) -> Unit,
    toggleSlideshow: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    if(activePhotoId <= 0) {
        ImageGrid(
            gridItems = gridItems,
            thumbnailSize = thumbSize,
            onSelectGridItem = { photo -> navigateToPhoto(category.id, photo.id) }
        )
    } else {
        PhotoPager(
            activePhotoIndex = activePhotoIndex,
            category = category,
            photos = photos,
            showPositionAndCount = true,
            showYearAndCategory = false,
            isSlideshowPlaying = isSlideshowPlaying,
            showDetails = showBottomSheet,
            sheetState = sheetState,
            navigateToYear = { },
            navigateToCategory = { },
            toggleSlideshow = toggleSlideshow,
            sharePhoto = { },
            toggleDetails = { showBottomSheet = true },
            updateCurrentPhoto = updateActivePhoto
        )
    }
}

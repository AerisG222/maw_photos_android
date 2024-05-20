package us.mikeandwan.photos.ui.screens.category

import android.graphics.drawable.Drawable
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
import us.mikeandwan.photos.ui.controls.loading.Loading
import us.mikeandwan.photos.ui.controls.metadata.CommentState
import us.mikeandwan.photos.ui.controls.metadata.ExifState
import us.mikeandwan.photos.ui.controls.metadata.RatingState
import us.mikeandwan.photos.ui.controls.photopager.PhotoPager
import java.io.File

const val CategoryRoute = "category"
private const val categoryIdArg = "categoryId"
private const val photoIdArg = "photoId"

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
        val state = rememberCategoryState(vm, categoryId, photoId)

        LaunchedEffect(Unit) {
            setNavArea(NavigationArea.Category)
        }

        val gridItems by vm.gridItems.collectAsStateWithLifecycle()
        val thumbSize by vm.gridItemThumbnailSize.collectAsStateWithLifecycle()
        val activePhotoId by vm.activePhotoId.collectAsStateWithLifecycle()
        val activePhotoIndex by vm.activePhotoIndex.collectAsStateWithLifecycle()
        val isSlideshowPlaying by vm.isSlideshowPlaying.collectAsStateWithLifecycle()
        val showDetailSheet by vm.showDetailSheet.collectAsStateWithLifecycle()


        when(state) {
            is CategoryState.Loading -> {
                Loading()
            }
            is CategoryState.CategoryLoaded -> {
                LaunchedEffect(state.category) {
                    updateTopBar(true, true, state.category.name)
                }

                Loading()
            }
            is CategoryState.Loaded -> {
                LaunchedEffect(state.category) {
                    updateTopBar(true, true, state.category.name)
                }

                CategoryScreen(
                    state.category,
                    gridItems,
                    thumbSize,
                    state.photos,
                    activePhotoId,
                    activePhotoIndex,
                    isSlideshowPlaying,
                    showDetailSheet,
                    ratingState = state.ratingState,
                    exifState = state.exifState,
                    commentState = state.commentState,
                    navigateToPhoto = navigateToPhoto,
                    updateActivePhoto = { newPhotoId -> vm.setActivePhotoId(newPhotoId) },
                    toggleSlideshow = { vm.toggleSlideshow() },
                    toggleShowDetails = { vm.toggleShowDetails() },
                    savePhotoToShare = { drawable, filename, onComplete ->
                        vm.saveFileToShare(drawable, filename, onComplete)
                    }
                )
            }
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

@Composable
fun CategoryScreen(
    category: PhotoCategory,
    gridItems: List<ImageGridItem>,
    thumbSize: GridThumbnailSize,
    photos: List<Photo>,
    activePhotoId: Int,
    activePhotoIndex: Int,
    isSlideshowPlaying: Boolean,
    showDetails: Boolean,
    navigateToPhoto: (categoryId: Int, photoId: Int) -> Unit,
    updateActivePhoto: (photoId: Int) -> Unit,
    toggleSlideshow: () -> Unit,
    toggleShowDetails: () -> Unit,
    savePhotoToShare: (drawable: Drawable, filename: String, onComplete: (File) -> Unit) -> Unit,
    ratingState: RatingState,
    exifState: ExifState,
    commentState: CommentState
) {
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
            showDetails = showDetails,
            navigateToYear = { },
            navigateToCategory = { },
            toggleSlideshow = toggleSlideshow,
            savePhotoToShare = savePhotoToShare,
            toggleDetails = toggleShowDetails,
            updateCurrentPhoto = updateActivePhoto,
            ratingState = ratingState,
            exifState = exifState,
            commentState = commentState
        )
    }
}

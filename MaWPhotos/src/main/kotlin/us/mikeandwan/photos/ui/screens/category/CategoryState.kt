package us.mikeandwan.photos.ui.screens.category

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import us.mikeandwan.photos.domain.models.Photo
import us.mikeandwan.photos.domain.models.PhotoCategory
import us.mikeandwan.photos.ui.controls.imagegrid.ImageGridState
import us.mikeandwan.photos.ui.controls.imagegrid.rememberImageGridState

sealed class CategoryState {
    data object Loading: CategoryState()

    data class CategoryLoaded(
        val category: PhotoCategory
    ): CategoryState()

    data class Loaded(
        val category: PhotoCategory,
        val gridState: ImageGridState
    ): CategoryState()
}

@Composable
fun rememberCategoryState(
    vm: CategoryViewModel,
    categoryId: Int,
    navigateToCategoryPhoto: (categoryId: Int, photoId: Int) -> Unit
): CategoryState {
    LaunchedEffect(categoryId) {
        vm.loadCategory(categoryId)
        vm.loadPhotos(categoryId)
    }

    val category by vm.category.collectAsStateWithLifecycle()
    val photos by vm.photos.collectAsStateWithLifecycle()
    val gridItems by vm.gridItems.collectAsStateWithLifecycle()
    val thumbSize by vm.gridItemThumbnailSize.collectAsStateWithLifecycle()

    val gridState = rememberImageGridState(
        gridItems = gridItems,
        thumbnailSize = thumbSize,
        onSelectGridItem = { item ->
            val photo = item.data as Photo
            navigateToCategoryPhoto(photo.categoryId, photo.id)
        }
    )

    return if(category == null) {
        CategoryState.Loading
    } else if(photos.isEmpty()) {
        CategoryState.CategoryLoaded(category!!)
    } else {
        CategoryState.Loaded(
            category!!,
            gridState
        )
    }
}

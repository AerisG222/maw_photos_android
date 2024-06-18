package us.mikeandwan.photos.ui.screens.category

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import us.mikeandwan.photos.domain.models.Media
import us.mikeandwan.photos.domain.models.MediaCategory
import us.mikeandwan.photos.domain.models.MediaType
import us.mikeandwan.photos.ui.controls.imagegrid.ImageGridState
import us.mikeandwan.photos.ui.controls.imagegrid.rememberImageGridState

sealed class CategoryState {
    data object Loading: CategoryState()

    data class CategoryLoaded(
        val category: MediaCategory
    ): CategoryState()

    data class Loaded(
        val category: MediaCategory,
        val gridState: ImageGridState<Media>
    ): CategoryState()
}

@Composable
fun rememberCategoryState(
    vm: CategoryViewModel,
    mediaType: MediaType,
    categoryId: Int,
    navigateToMedia: (Media) -> Unit
): CategoryState {
    LaunchedEffect(categoryId) {
        vm.loadCategory(mediaType, categoryId)
        vm.loadMedia(mediaType, categoryId)
    }

    val category by vm.category.collectAsStateWithLifecycle()
    val photos by vm.media.collectAsStateWithLifecycle()
    val gridItems by vm.gridItems.collectAsStateWithLifecycle()
    val thumbSize by vm.gridItemThumbnailSize.collectAsStateWithLifecycle()

    val gridState = rememberImageGridState(
        gridItems = gridItems,
        thumbnailSize = thumbSize,
        onSelectGridItem = { navigateToMedia(it.data) }
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

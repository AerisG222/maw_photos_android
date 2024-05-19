package us.mikeandwan.photos.ui.screens.category

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import us.mikeandwan.photos.domain.models.Photo
import us.mikeandwan.photos.domain.models.PhotoCategory
import us.mikeandwan.photos.ui.controls.metadata.RatingState
import us.mikeandwan.photos.ui.controls.metadata.rememberRatingState

class CategoryState(
    val category: PhotoCategory?,
    val photos: List<Photo>,
    val ratingState: RatingState
)

@Composable
fun rememberCategoryState(
    vm: CategoryViewModel,
    categoryId: Int,
    photoId: Int
): CategoryState {
    LaunchedEffect(categoryId) {
        vm.loadCategory(categoryId)
        vm.loadPhotos(categoryId)
    }

    val category by vm.category.collectAsStateWithLifecycle()
    val photos by vm.photos.collectAsStateWithLifecycle()

    LaunchedEffect(category, photos, photoId) {
        if(photos.isNotEmpty() && photoId > 0) {
            vm.setActivePhotoId(photoId)
        }
    }

    val userRating by vm.userRating.collectAsStateWithLifecycle()
    val averageRating by vm.averageRating.collectAsStateWithLifecycle()

    val ratingState = rememberRatingState(
        userRating = userRating,
        averageRating = averageRating,
        updateUserRating = { vm.setRating(it) }
    )

    return CategoryState(
        category,
        photos,
        ratingState
    )
}

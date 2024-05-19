package us.mikeandwan.photos.ui.screens.category

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import us.mikeandwan.photos.ui.controls.metadata.RatingState
import us.mikeandwan.photos.ui.controls.metadata.rememberRatingState

class CategoryState(
    val ratingState: RatingState
)

@Composable
fun rememberCategoryState(vm: CategoryViewModel): CategoryState {
    val userRating by vm.userRating.collectAsStateWithLifecycle()
    val averageRating by vm.averageRating.collectAsStateWithLifecycle()

    val ratingState = rememberRatingState(
        userRating = userRating,
        averageRating = averageRating,
        updateUserRating = { vm.setRating(it) }
    )

    return CategoryState(ratingState)
}

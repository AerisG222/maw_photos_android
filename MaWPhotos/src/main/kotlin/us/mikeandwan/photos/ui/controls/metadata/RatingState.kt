package us.mikeandwan.photos.ui.controls.metadata

import androidx.compose.runtime.Composable

class RatingState(
    val userRating: Short,
    val averageRating: Float,
    val updateUserRating: (rating: Short) -> Unit
)

@Composable
fun rememberRatingState(
    userRating: Short = 0,
    averageRating: Float = 0f,
    updateUserRating: (rating: Short) -> Unit = {}
) : RatingState {
    return RatingState(
        userRating = userRating,
        averageRating = averageRating,
        updateUserRating = updateUserRating
    )
}

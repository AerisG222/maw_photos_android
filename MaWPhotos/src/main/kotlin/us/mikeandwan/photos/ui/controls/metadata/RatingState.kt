package us.mikeandwan.photos.ui.controls.metadata

import androidx.compose.runtime.Composable

class RatingState(
    val userRating: Short,
    val averageRating: Float,
    val fetchRating: () -> Unit = {},
    val updateUserRating: (rating: Short) -> Unit
)

@Composable
fun rememberRatingState(
    userRating: Short = 0,
    averageRating: Float = 0f,
    fetchRating: () -> Unit = {},
    updateUserRating: (rating: Short) -> Unit = {}
) : RatingState {
    return RatingState(
        userRating = userRating,
        averageRating = averageRating,
        fetchRating = fetchRating,
        updateUserRating = updateUserRating
    )
}

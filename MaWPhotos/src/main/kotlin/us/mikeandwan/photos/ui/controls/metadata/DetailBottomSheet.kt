package us.mikeandwan.photos.ui.controls.metadata

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailBottomSheet(
    sheetState: SheetState,
    activePhotoId: Int,
    ratingState: RatingState,
    exifState: ExifState,
    commentState: CommentState,
    fetchRatingDetails: () -> Unit,
    fetchExifDetails: () -> Unit,
    fetchCommentDetails: () -> Unit,
    onDismissRequest: () -> Unit
) {
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = { onDismissRequest() }
    ) {
        DetailTabs(
            activePhotoId = activePhotoId,
            ratingState = ratingState,
            exifState = exifState,
            commentState = commentState,
            fetchRatingDetails = fetchRatingDetails,
            fetchExifDetails = fetchExifDetails,
            fetchCommentDetails = fetchCommentDetails
        )
    }
}

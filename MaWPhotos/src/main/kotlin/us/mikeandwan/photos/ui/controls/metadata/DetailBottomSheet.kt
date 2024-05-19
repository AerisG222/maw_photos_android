package us.mikeandwan.photos.ui.controls.metadata

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import us.mikeandwan.photos.domain.models.PhotoComment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailBottomSheet(
    sheetState: SheetState,
    activePhotoId: Int,
    ratingState: RatingState,
    exif: List<Pair<String, String>>,
    comments: List<PhotoComment>,
    addComment: (String) -> Unit,
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
            exif = exif,
            comments = comments,
            addComment = addComment,
            fetchRatingDetails = fetchRatingDetails,
            fetchExifDetails = fetchExifDetails,
            fetchCommentDetails = fetchCommentDetails
        )
    }
}

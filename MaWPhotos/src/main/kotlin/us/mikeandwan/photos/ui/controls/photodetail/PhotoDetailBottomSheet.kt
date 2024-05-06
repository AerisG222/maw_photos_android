package us.mikeandwan.photos.ui.controls.photodetail

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import us.mikeandwan.photos.domain.models.PhotoComment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoDetailBottomSheet(
    sheetState: SheetState,
    userRating: Short,
    averageRating: Float,
    exif: List<Pair<String, String>>,
    comments: List<PhotoComment>,
    addComment: (String) -> Unit,
    setRating: (Short) -> Unit,
    fetchRatingDetails: () -> Unit,
    fetchExifDetails: () -> Unit,
    fetchCommentDetails: () -> Unit,
    onDismissRequest: () -> Unit
) {
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = { onDismissRequest() }
    ) {
        PhotoDetailTabs(
            userRating = userRating,
            averageRating = averageRating,
            exif = exif,
            comments = comments,
            addComment = addComment,
            setRating = setRating,
            fetchRatingDetails = fetchRatingDetails,
            fetchExifDetails = fetchExifDetails,
            fetchCommentDetails = fetchCommentDetails
        )
    }
}

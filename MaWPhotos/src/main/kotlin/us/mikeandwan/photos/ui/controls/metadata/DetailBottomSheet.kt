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
            activePhotoId = activePhotoId,
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

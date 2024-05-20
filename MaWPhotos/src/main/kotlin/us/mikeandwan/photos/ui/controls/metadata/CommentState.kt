package us.mikeandwan.photos.ui.controls.metadata

import androidx.compose.runtime.Composable
import us.mikeandwan.photos.domain.models.PhotoComment

class CommentState(
    val comments: List<PhotoComment>,
    val addComment: (String) -> Unit
)

@Composable
fun rememberCommentState(
    comments: List<PhotoComment> = emptyList(),
    addComment: (String) -> Unit = {}
): CommentState {
    return CommentState(
        comments = comments,
        addComment = addComment
    )
}

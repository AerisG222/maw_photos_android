package us.mikeandwan.photos.ui.controls.metadata

import androidx.compose.runtime.Composable
import us.mikeandwan.photos.domain.models.PhotoComment

class CommentState(
    val comments: List<PhotoComment>,
    val fetchComments: () -> Unit,
    val addComment: (String) -> Unit
)

@Composable
fun rememberCommentState(
    comments: List<PhotoComment> = emptyList(),
    fetchComments: () -> Unit = {},
    addComment: (String) -> Unit = {}
): CommentState {
    return CommentState(
        comments = comments,
        fetchComments = fetchComments,
        addComment = addComment
    )
}

package us.mikeandwan.photos.ui.controls.metadata

import androidx.compose.runtime.Composable
import us.mikeandwan.photos.domain.models.Comment

class CommentState(
    val comments: List<Comment>,
    val fetchComments: () -> Unit,
    val addComment: (String) -> Unit
)

@Composable
fun rememberCommentState(
    comments: List<Comment> = emptyList(),
    fetchComments: () -> Unit = {},
    addComment: (String) -> Unit = {}
): CommentState {
    return CommentState(
        comments = comments,
        fetchComments = fetchComments,
        addComment = addComment
    )
}

package us.mikeandwan.photos.ui.controls.photocomment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import us.mikeandwan.photos.R
import us.mikeandwan.photos.domain.models.PhotoComment

@Composable
fun PhotoCommentScreen(
    comments: List<PhotoComment>,
    addComment: (String) -> Unit
) {
    val (newComment, setNewComment) = remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxHeight()) {
        CommentTable(comments = comments)

        Row(modifier = Modifier
            .padding(8.dp, 8.dp)
            .fillMaxWidth()
        ) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = newComment,
                singleLine = false,
                minLines = 3,
                maxLines = 3,
                onValueChange = { setNewComment(it) }
            )
        }

        Row(modifier = Modifier
            .padding(0.dp, 0.dp, 0.dp, 8.dp)
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = { addComment(newComment) },
            ) {
                Text(
                    text = stringResource(id = R.string.frg_comment_add_comment)
                )
            }
        }
    }
}

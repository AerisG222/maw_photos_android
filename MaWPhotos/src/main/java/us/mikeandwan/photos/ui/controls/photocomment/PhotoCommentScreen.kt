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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import us.mikeandwan.photos.R
import us.mikeandwan.photos.ui.theme.AppTheme

@Composable
fun PhotoCommentScreen(
    viewModel: PhotoCommentViewModel = viewModel()
) {
    val comments = viewModel.comments.collectAsState()
    val newComment = viewModel.newComment.collectAsState()

    AppTheme {
        Column(modifier = Modifier.fillMaxHeight()) {
            CommentTable(comments = comments.value)

            Row(modifier = Modifier
                .padding(8.dp, 8.dp)
                .fillMaxWidth()
            ) {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = newComment.value,
                    singleLine = false,
                    minLines = 3,
                    maxLines = 3,
                    onValueChange = {
                        viewModel.setNewComment(it)
                    }
                )
            }

            Row(modifier = Modifier
                .padding(0.dp, 0.dp, 0.dp, 8.dp)
                .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { viewModel.addComment() },
                ) {
                    Text(
                        text = stringResource(id = R.string.frg_comment_add_comment)
                    )
                }
            }
        }
    }
}
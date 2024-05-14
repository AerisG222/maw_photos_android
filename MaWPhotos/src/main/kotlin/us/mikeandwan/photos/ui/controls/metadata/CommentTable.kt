package us.mikeandwan.photos.ui.controls.metadata

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import us.mikeandwan.photos.domain.models.PhotoComment
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun CommentTable(
    comments: List<PhotoComment>,
    footer: @Composable () -> Unit
) {
    val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val bgHead = MaterialTheme.colorScheme.surfaceVariant
    val txtHead = MaterialTheme.colorScheme.onSurfaceVariant
    val bgRow = MaterialTheme.colorScheme.surface
    val txtRow = MaterialTheme.colorScheme.onSurface

    LazyColumn {
        itemsIndexed(comments) { index, comment ->
            Row(modifier = Modifier
                .fillMaxWidth()
                .background(bgHead)
                .padding(4.dp, 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    maxLines = 1,
                    fontWeight = FontWeight.Bold,
                    color = txtHead,
                    text = comment.username
                )
                Text(
                    maxLines = 1,
                    fontWeight = FontWeight.Bold,
                    color = txtHead,
                    textAlign = TextAlign.End,
                    text = comment.entryDate
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                        .format(fmt)
                )
            }
            Row(modifier = Modifier
                .fillMaxWidth()
                .background(bgRow)
                .padding(4.dp, 2.dp)
            ) {
                Text(
                    color = txtRow,
                    text = comment.commentText
                )
            }

            if (index != comments.size - 1) {
                HorizontalDivider()
            }
        }

        item {
            footer()
        }
    }
}

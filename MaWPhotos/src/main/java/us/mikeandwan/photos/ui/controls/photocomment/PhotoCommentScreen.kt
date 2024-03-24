package us.mikeandwan.photos.ui.controls.photocomment

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

@Composable
fun PhotoCommentScreen() {
    Text(
        text = "COMMENT",
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxSize()
    )
}
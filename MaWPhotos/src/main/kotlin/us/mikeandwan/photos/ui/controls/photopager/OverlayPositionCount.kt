package us.mikeandwan.photos.ui.controls.photopager

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun OverlayPositionCount(
    position: Int,
    count: Int
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(text = position.toString())
        Text(text = " / ")
        Text(text = count.toString())
    }
}

package us.mikeandwan.photos.ui.controls.photoexif

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

@Composable
fun PhotoExifScreen() {
    Text(
        text = "EXIF",
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxSize()
    )
}
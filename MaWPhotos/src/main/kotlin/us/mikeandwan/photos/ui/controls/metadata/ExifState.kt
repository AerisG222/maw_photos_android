package us.mikeandwan.photos.ui.controls.metadata

import androidx.compose.runtime.Composable

class ExifState(
    val exifDisplay: List<Pair<String, String>>
)

@Composable
fun rememberExifState(
    exif: List<Pair<String, String>> = emptyList()
): ExifState {
    return ExifState(
        exifDisplay = exif
    )
}

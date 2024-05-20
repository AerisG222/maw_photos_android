package us.mikeandwan.photos.ui.controls.metadata

import androidx.compose.runtime.Composable

class ExifState(
    val exifDisplay: List<Pair<String, String>>,
    val fetchExif: () -> Unit = {},
)

@Composable
fun rememberExifState(
    exif: List<Pair<String, String>> = emptyList(),
    fetchExif: () -> Unit = {},
): ExifState {
    return ExifState(
        exifDisplay = exif,
        fetchExif = fetchExif
    )
}

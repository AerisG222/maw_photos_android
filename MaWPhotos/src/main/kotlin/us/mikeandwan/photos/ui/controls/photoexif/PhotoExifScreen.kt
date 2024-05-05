package us.mikeandwan.photos.ui.controls.photoexif

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PhotoExifScreen(
    exif: List<Pair<String, String>>
) {
    LazyColumn(Modifier.fillMaxSize()) {
        itemsIndexed(exif) { index, data ->
            val bgColor = if (index % 2 == 0) { Color.DarkGray } else { Color.Gray }

            Row(
                Modifier
                    .fillMaxWidth()
                    .background(bgColor)
            ) {
                Text (
                    text = data.first,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp, 2.dp)
                )
                Text (
                    text = data.second,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp, 2.dp)
                )
            }
        }
    }
}

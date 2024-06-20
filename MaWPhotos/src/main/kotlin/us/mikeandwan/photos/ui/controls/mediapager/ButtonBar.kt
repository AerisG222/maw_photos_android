package us.mikeandwan.photos.ui.controls.mediapager

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import us.mikeandwan.photos.R
import us.mikeandwan.photos.domain.models.MediaType

@Composable
fun ButtonBar(
    activeMediaType: MediaType,
    isSlideshowPlaying: Boolean,
    onRotateLeft: () -> Unit,
    onRotateRight: () -> Unit,
    onToggleSlideshow: () -> Unit,
    onShare: () -> Unit,
    onViewDetails: () -> Unit
) {
    val slideshowIcon = if(isSlideshowPlaying) R.drawable.ic_stop else R.drawable.ic_play_arrow
    val color = MaterialTheme.colorScheme.onSurface

    Row(
        horizontalArrangement = Arrangement.Absolute.SpaceAround,
        modifier = Modifier
            .padding(2.dp, 8.dp, 2.dp, 8.dp)
            .fillMaxWidth()
    ) {
        if(activeMediaType == MediaType.Photo) {
            IconButton(onClick = onRotateLeft) {
                AsyncImage(
                    model = R.drawable.ic_rotate_left,
                    contentDescription = stringResource(id = R.string.rotate_left_icon_description),
                    modifier = Modifier.size(48.dp),
                    colorFilter = ColorFilter.tint(color)
                )
            }

            IconButton(onClick = onRotateRight) {
                AsyncImage(
                    model = R.drawable.ic_rotate_right,
                    contentDescription = stringResource(id = R.string.rotate_right_icon_description),
                    modifier = Modifier.size(48.dp),
                    colorFilter = ColorFilter.tint(color)
                )
            }

            IconButton(onClick = onToggleSlideshow) {
                AsyncImage(
                    model = slideshowIcon,
                    contentDescription = stringResource(id = R.string.toggle_slideshow_icon_description),
                    modifier = Modifier.size(48.dp),
                    colorFilter = ColorFilter.tint(color)
                )
            }

            IconButton(onClick = onShare) {
                AsyncImage(
                    model = R.drawable.ic_share,
                    contentDescription = stringResource(id = R.string.toggle_slideshow_icon_description),
                    modifier = Modifier.size(48.dp),
                    colorFilter = ColorFilter.tint(color)
                )
            }
        }

        IconButton(onClick = onViewDetails) {
            AsyncImage(
                model = R.drawable.ic_keyboard_double_arrow_up,
                contentDescription = stringResource(id = R.string.view_media_details_icon_description),
                modifier = Modifier.size(48.dp),
                colorFilter = ColorFilter.tint(color)
            )
        }
    }
}

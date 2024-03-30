package us.mikeandwan.photos.ui.screens.photo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImage
import us.mikeandwan.photos.R

@Composable
fun ButtonBar(
    isSlideshowPlaying: Boolean,
    onRotateLeft: () -> Unit,
    onRotateRight: () -> Unit,
    onToggleSlideshow: () -> Unit,
    onShare: () -> Unit,
    onViewDetails: () -> Unit
) {
    val slideshowIcon = if(isSlideshowPlaying) R.drawable.ic_stop else R.drawable.ic_play_arrow

    Row(
        horizontalArrangement = Arrangement.Absolute.SpaceAround,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        AsyncImage(
            model = R.drawable.ic_rotate_left,
            contentDescription = stringResource(id = R.string.rotate_left_icon_description),
            modifier = Modifier
                .clickable { onRotateLeft() }
        )

        AsyncImage(
            model = R.drawable.ic_rotate_right,
            contentDescription = stringResource(id = R.string.rotate_right_icon_description),
            modifier = Modifier
                .clickable { onRotateRight() }
        )

        AsyncImage(
            model = slideshowIcon,
            contentDescription = stringResource(id = R.string.toggle_slideshow_icon_description),
            modifier = Modifier
                .clickable { onToggleSlideshow() }
        )

        AsyncImage(
            model = R.drawable.ic_share,
            contentDescription = stringResource(id = R.string.toggle_slideshow_icon_description),
            modifier = Modifier
                .clickable { onShare() }
        )

        AsyncImage(
            model = R.drawable.ic_keyboard_double_arrow_up,
            contentDescription = stringResource(id = R.string.view_photo_details_icon_description),
            modifier = Modifier
                .clickable { onViewDetails() }
        )
    }
}

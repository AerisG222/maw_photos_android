package us.mikeandwan.photos.ui.controls.imagegrid

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import us.mikeandwan.photos.R
import us.mikeandwan.photos.domain.models.MediaCategory
import us.mikeandwan.photos.domain.models.MediaType
import us.mikeandwan.photos.domain.models.Video

@Composable
fun <T> ImageGridImage(
    item: ImageGridItem<T>,
    size: Dp,
    onSelectImage: (ImageGridItem<T>) -> Unit,
    modifier: Modifier = Modifier
) {
    val showVideoBadge = when(item.data) {
        is MediaCategory -> item.data.type == MediaType.Video
        is Video -> true
        else -> false
    }

    Box {
        AsyncImage(
            model = item.url,
            contentDescription = stringResource(id = R.string.li_category_thumbnail_description),
            placeholder = painterResource(id = R.drawable.ic_placeholder),
            error = painterResource(id = R.drawable.ic_broken_image),
            contentScale = ContentScale.Crop,
            modifier = modifier
                .height(size)
                .clickable {
                    onSelectImage(item)
                }
        )

        if(showVideoBadge) {
            AsyncImage(
                model = R.drawable.mdi_video,
                contentDescription = stringResource(id = R.string.li_category_thumbnail_description),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                modifier = modifier
                    .align(Alignment.TopStart)
                    .padding(2.dp)
                    .size(16.dp)
            )
        }
    }
}

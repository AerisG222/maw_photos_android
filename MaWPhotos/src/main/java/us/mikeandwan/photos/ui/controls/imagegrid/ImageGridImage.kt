package us.mikeandwan.photos.ui.controls.imagegrid

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import coil.compose.AsyncImage
import us.mikeandwan.photos.R
import us.mikeandwan.photos.ui.ImageGridClickListener

@Composable
fun ImageGridImage(
    item: ImageGridItem,
    size: Dp,
    onSelectImage: ImageGridClickListener?,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = item.url,
        contentDescription = stringResource(id = R.string.li_category_thumbnail_description),
        placeholder = painterResource(id = R.drawable.ic_placeholder),
        error = painterResource(id = R.drawable.ic_broken_image),
        contentScale = ContentScale.Crop,
        modifier = modifier
            .height(size)
            .clickable {
                onSelectImage?.onClick(item)
            }
    )
}

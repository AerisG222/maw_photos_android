package us.mikeandwan.photos.ui.controls.imagegrid

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import us.mikeandwan.photos.R
import us.mikeandwan.photos.ui.ImageGridClickListener

@Composable
fun ImageGridImage(
    item: ImageGridItemWithSize,
    onSelectImage: ImageGridClickListener?,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = item.url,
        contentDescription = stringResource(id = R.string.li_category_thumbnail_description),
        placeholder = painterResource(id = R.drawable.ic_placeholder),
        error = painterResource(id = R.drawable.ic_broken_image),
        modifier = modifier
            .padding(0.dp, 0.dp, 0.dp, 2.dp)
            .clickable {
                onSelectImage?.onClick(item)
            }
    )
}

package us.mikeandwan.photos.ui.screens.upload

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import us.mikeandwan.photos.R
import androidx.lifecycle.viewmodel.compose.viewModel
import us.mikeandwan.photos.domain.models.GridThumbnailSize
import us.mikeandwan.photos.ui.controls.imagegrid.ImageGrid
import us.mikeandwan.photos.ui.controls.imagegrid.ImageGridItem

@Composable
fun UploadScreen(
    viewModel: UploadViewModel = viewModel()
) {
    val photos = viewModel.filesToUpload.collectAsState()

    AsyncImage(
        model = R.drawable.ic_share,
        contentDescription = stringResource(id = R.string.share_photo_icon_description)
    )

    ImageGrid(
        gridItems = photos.value.mapIndexed { id, file -> ImageGridItem(id, file.path, file) },
        thumbnailSize = GridThumbnailSize.Medium,
        onSelectGridItem = { }
    )

    Text(
        modifier = Modifier.padding(8.dp),
        text = stringResource(id = R.string.upload_queue_icon_description)
    )
}

package us.mikeandwan.photos.ui.screens.upload

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import coil.compose.AsyncImage
import us.mikeandwan.photos.R
import us.mikeandwan.photos.domain.models.GridThumbnailSize
import us.mikeandwan.photos.domain.models.NavigationArea
import us.mikeandwan.photos.ui.controls.imagegrid.ImageGrid
import us.mikeandwan.photos.ui.controls.imagegrid.ImageGridItem
import java.io.File

const val UploadRoute = "upload"

fun NavGraphBuilder.uploadScreen(
    updateTopBar : (Boolean, Boolean, String) -> Unit,
    setNavArea: (NavigationArea) -> Unit
) {
    composable(UploadRoute) {
        val vm: UploadViewModel = hiltViewModel()
        val files by vm.filesToUpload.collectAsStateWithLifecycle()

        updateTopBar(true, false, "Upload Queue")
        setNavArea(NavigationArea.Upload)

        UploadScreen(
            files
        )
    }
}

fun NavController.navigateToUpload() {
    this.navigate(UploadRoute)
}

@Composable
fun UploadScreen(
    files: List<File>
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        AsyncImage(
            model = R.drawable.ic_share,
            contentDescription = stringResource(id = R.string.share_photo_icon_description)
        )
    }

    Box {
        ImageGrid(
            gridItems = files.mapIndexed { id, file -> ImageGridItem(id, file.path, file) },
            thumbnailSize = GridThumbnailSize.Medium,
            onSelectGridItem = { }
        )
    }
}

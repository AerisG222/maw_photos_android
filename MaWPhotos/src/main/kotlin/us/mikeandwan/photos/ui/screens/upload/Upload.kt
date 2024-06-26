package us.mikeandwan.photos.ui.screens.upload

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import coil.compose.AsyncImage
import kotlinx.serialization.Serializable
import us.mikeandwan.photos.R
import us.mikeandwan.photos.domain.models.GridThumbnailSize
import us.mikeandwan.photos.domain.models.NavigationArea
import us.mikeandwan.photos.ui.controls.imagegrid.ImageGrid
import us.mikeandwan.photos.ui.controls.imagegrid.ImageGridItem
import us.mikeandwan.photos.ui.controls.imagegrid.rememberImageGridState
import java.io.File

@Serializable
object UploadRoute

fun NavGraphBuilder.uploadScreen(
    updateTopBar : (Boolean, Boolean, String) -> Unit,
    setNavArea: (NavigationArea) -> Unit,
    navigateToLogin: () -> Unit
) {
    composable<UploadRoute> {
        val vm: UploadViewModel = hiltViewModel()
        val files by vm.filesToUpload.collectAsStateWithLifecycle()

        val isAuthorized by vm.isAuthorized.collectAsStateWithLifecycle()

        LaunchedEffect(isAuthorized) {
            if(!isAuthorized) {
                navigateToLogin()
            }
        }

        LaunchedEffect(Unit) {
            updateTopBar(true, false, "Upload Queue")
            setNavArea(NavigationArea.Upload)
        }

        UploadScreen(
            files
        )
    }
}

@Composable
fun UploadScreen(
    files: List<File>
) {
    val gridState = rememberImageGridState(
        gridItems = files.mapIndexed { id, file -> ImageGridItem(id, file.path, file) },
        thumbnailSize = GridThumbnailSize.Medium,
        onSelectGridItem = { }
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        AsyncImage(
            model = R.drawable.ic_share,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
            contentDescription = stringResource(id = R.string.share_photo_icon_description),
            modifier = Modifier
                .padding(40.dp)
                .fillMaxSize()
        )
    }

    Box {
        ImageGrid(gridState)
    }
}

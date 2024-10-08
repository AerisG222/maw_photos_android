package us.mikeandwan.photos.ui.screens.random

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import us.mikeandwan.photos.domain.models.GridThumbnailSize
import us.mikeandwan.photos.domain.models.Media
import us.mikeandwan.photos.domain.models.NavigationArea
import us.mikeandwan.photos.domain.models.Photo
import us.mikeandwan.photos.ui.controls.imagegrid.ImageGrid
import us.mikeandwan.photos.ui.controls.imagegrid.ImageGridItem
import us.mikeandwan.photos.ui.controls.imagegrid.rememberImageGridState
import us.mikeandwan.photos.ui.controls.topbar.TopBarState
import us.mikeandwan.photos.ui.toImageGridItem

@Serializable
object RandomRoute

fun NavGraphBuilder.randomScreen(
    navigateToPhoto: (Int) -> Unit,
    updateTopBar : (TopBarState) -> Unit,
    setNavArea: (NavigationArea) -> Unit,
    navigateToLogin: () -> Unit
) {
    composable<RandomRoute> {
        val vm: RandomViewModel = hiltViewModel()

        val isAuthorized by vm.isAuthorized.collectAsStateWithLifecycle()
        val photos by vm.media.collectAsStateWithLifecycle()
        val thumbSize by vm.gridItemThumbnailSize.collectAsStateWithLifecycle()

        LaunchedEffect(isAuthorized) {
            if(!isAuthorized) {
                navigateToLogin()
            }
        }

        LaunchedEffect(Unit) {
            vm.initialFetch(24)
        }

        DisposableEffect(Unit) {
            vm.onResume()

            onDispose {
                vm.onPause()
            }
        }

        RandomScreen(
            photos,
            thumbSize,
            onPhotoClicked = { navigateToPhoto(it.id) },
            updateTopBar,
            setNavArea
        )
    }
}

@Composable
fun RandomScreen(
    photos: List<Photo>,
    thumbSize: GridThumbnailSize,
    onPhotoClicked: (ImageGridItem<Media>) -> Unit,
    updateTopBar : (TopBarState) -> Unit,
    setNavArea: (NavigationArea) -> Unit,
) {
    LaunchedEffect(Unit) {
        setNavArea(NavigationArea.Random)

        updateTopBar(
            TopBarState().copy(
                title = "Random"
            )
        )
    }

    val gridState = rememberImageGridState(
        photos.map { it.toImageGridItem() },
        thumbSize,
        onPhotoClicked
    )

    ImageGrid(gridState)
}

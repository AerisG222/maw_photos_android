package us.mikeandwan.photos.ui.controls.photopager

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import coil.compose.AsyncImage
import net.engawapg.lib.zoomable.ScrollGesturePropagation
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import us.mikeandwan.photos.domain.models.Photo

@Composable
fun PhotoPager(
    photos: List<Photo>,
    activeIndex: Int,
    activeRotation: Float = 0f,
    setActiveIndex: (Int) -> Unit
) {
    val pagerState = rememberPagerState(
        pageCount = { photos.size },
        initialPage = activeIndex
    )
    val zoomState = rememberZoomState()

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            if(page >= 0) {
                setActiveIndex(page)
            }
        }
    }

    LaunchedEffect(activeIndex) {
        pagerState.animateScrollToPage(activeIndex)
    }

    HorizontalPager(
        state = pagerState,
        contentPadding = PaddingValues(0.dp),
        userScrollEnabled = true,
        modifier = Modifier.fillMaxSize()
    ) { index ->
        AsyncImage(
            model = photos[index].mdUrl,
            contentDescription = "",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .zoomable(
                    zoomState,
                    scrollGesturePropagation = ScrollGesturePropagation.NotZoomed
                )
                .graphicsLayer {
                    val pageOffset =
                        (pagerState.currentPage - index) +
                                pagerState.currentPageOffsetFraction

                    alpha = lerp(
                        start = 0.4f,
                        stop = 1f,
                        fraction = 1f - pageOffset.coerceIn(0f, 1f),
                    )

                    cameraDistance = 8 * density
                    rotationY = lerp(
                        start = 0f,
                        stop = 40f,
                        fraction = pageOffset.coerceIn(-1f, 1f),
                    )

                    lerp(
                        start = 0.5f,
                        stop = 1f,
                        fraction = 1f - pageOffset.coerceIn(0f, 1f),
                    ).also { scale ->
                        scaleX = scale
                        scaleY = scale
                    }
                }
                .rotate(activeRotation)
        )
    }
}

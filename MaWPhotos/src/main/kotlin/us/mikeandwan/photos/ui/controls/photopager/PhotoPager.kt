package us.mikeandwan.photos.ui.controls.photopager

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import coil.compose.AsyncImage
import net.engawapg.lib.zoomable.ScrollGesturePropagation
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import us.mikeandwan.photos.domain.models.Photo
import us.mikeandwan.photos.domain.models.PhotoCategory
import us.mikeandwan.photos.ui.controls.photodetail.PhotoDetailBottomSheet

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PhotoPager(
    activePhotoIndex: Int,
    category: PhotoCategory?,
    photos: List<Photo>,
    showPositionAndCount: Boolean,
    showYearAndCategory: Boolean,
    isSlideshowPlaying: Boolean,
    showDetails: Boolean,
    navigateToYear: (Int) -> Unit,
    navigateToCategory: (PhotoCategory) -> Unit,
    rotateLeft: () -> Unit,
    rotateRight: () -> Unit,
    toggleSlideshow: () -> Unit,
    sharePhoto: () -> Unit,
    toggleDetails: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { photos.size })
    val zoomState = rememberZoomState()

    HorizontalPager(
        state = pagerState,
        contentPadding = PaddingValues(0.dp),
        userScrollEnabled = true,
        pageContent = { index ->
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
            )
        },
        modifier = Modifier
            .fillMaxSize()
    )

    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxSize()
    ) {
        if(showYearAndCategory || showPositionAndCount) {
            Row(modifier = Modifier.fillMaxWidth()) {
                if(showYearAndCategory && category != null) {
                    OverlayYearName(
                        category = category,
                        onClickYear = { year -> navigateToYear(year) },
                        onClickCategory = { category -> navigateToCategory(category) })
                }

                if(showPositionAndCount) {
                    OverlayPositionCount(
                        position = activePhotoIndex + 1,
                        count = photos.size
                    )
                }
            }
        }

        Row(modifier = Modifier
            .height(40.dp)
            .fillMaxWidth()
            .padding(2.dp, 4.dp)
        ) {
            ButtonBar(
                isSlideshowPlaying = isSlideshowPlaying,
                onRotateLeft = rotateLeft,
                onRotateRight = rotateRight,
                onToggleSlideshow = toggleSlideshow,
                onShare = sharePhoto,
                onViewDetails = toggleDetails
            )
        }
    }

    if(showDetails) {
        PhotoDetailBottomSheet(
            onDismissRequest = toggleDetails
        )
    }
}


//private suspend fun sharePhoto(photo: Photo) {
//    viewModel.sharePhotoComplete()
//
//    val drawable = getPhotoToShare(photo)
//    val fileToShare = viewModel.savePhotoToShare(drawable, getFilenameFromUrl(photo.mdUrl))
//    val contentUri = FileProvider.getUriForFile(requireActivity(), "us.mikeandwan.photos.fileprovider", fileToShare)
//    val sendIntent = Intent(Intent.ACTION_SEND)
//
//    sendIntent.setDataAndType(contentUri, "image/*")
//    sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//    sendIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
//
//    val shareIntent = Intent.createChooser(sendIntent, null)
//
//    startActivity(shareIntent)
//}
//
//private suspend fun getPhotoToShare(photo: Photo): Drawable {
//    return withContext(Dispatchers.IO) {
//        val loader = ImageLoader(requireActivity())
//        val request = ImageRequest.Builder(requireActivity())
//            .data(photo.mdUrl)
//            .allowHardware(false) // Disable hardware bitmaps.
//            .build()
//
//        (loader.execute(request) as SuccessResult).drawable
//    }
//}
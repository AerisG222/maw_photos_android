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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
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
import us.mikeandwan.photos.domain.models.PhotoCategory
import us.mikeandwan.photos.ui.controls.photodetail.PhotoDetailBottomSheet

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PhotoPager(
    activePhotoIndex: Int,
    category: PhotoCategory?,
    photos: List<Photo>,
    showPositionAndCount: Boolean,
    showYearAndCategory: Boolean,
    isSlideshowPlaying: Boolean,
    showDetails: Boolean,
    sheetState: SheetState,
    navigateToYear: (Int) -> Unit,
    navigateToCategory: (PhotoCategory) -> Unit,
    updateCurrentPhoto: (photoId: Int) -> Unit,
    toggleSlideshow: () -> Unit,
    sharePhoto: () -> Unit,
    toggleDetails: () -> Unit
) {
    val pagerState = rememberPagerState(
        pageCount = { photos.size },
        initialPage = activePhotoIndex
    )
    val zoomState = rememberZoomState()
    val rotationDictionary = remember { HashMap<Int,Float>() }
    val (activeRotation, setActiveRotation) = remember { mutableFloatStateOf(0f) }

    fun getRotationForPage(page: Int): Float {
        return when(rotationDictionary.containsKey(page)) {
            true -> rotationDictionary[page]!!
            false -> 0f
        }
    }

    fun updateRotation(deg: Float) {
        val page = pagerState.currentPage
        val currRotation = getRotationForPage(page)
        val newRotation = currRotation + deg

        rotationDictionary[page] = newRotation
        setActiveRotation(newRotation)
    }

    LaunchedEffect(activePhotoIndex) {
        pagerState.animateScrollToPage(activePhotoIndex)
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            setActiveRotation(getRotationForPage(page))
            updateCurrentPhoto(photos[page].id)
        }
    }

    HorizontalPager(
        state = pagerState,
        contentPadding = PaddingValues(0.dp),
        userScrollEnabled = true,
        modifier = Modifier
            .fillMaxSize()
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

    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxSize()
    ) {
        if(showYearAndCategory || showPositionAndCount) {
            val ha = if(showYearAndCategory) Arrangement.SpaceBetween else Arrangement.End

            Row(horizontalArrangement = ha,
                modifier = Modifier.fillMaxWidth()) {
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
                onRotateLeft = { updateRotation(-90f) },
                onRotateRight = { updateRotation(90f) },
                onToggleSlideshow = toggleSlideshow,
                onShare = sharePhoto,
                onViewDetails = toggleDetails
            )
        }
    }

    if(showDetails) {
        PhotoDetailBottomSheet(
            sheetState = sheetState,
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

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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
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

    HorizontalPager(
        state = pagerState,
        beyondBoundsPageCount = 2,
        contentPadding = PaddingValues(0.dp),
        userScrollEnabled = true,
        pageContent = {
            AsyncImage(
                model = photos[it],
                contentDescription = "",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
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
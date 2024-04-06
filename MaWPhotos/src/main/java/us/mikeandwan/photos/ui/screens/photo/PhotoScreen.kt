package us.mikeandwan.photos.ui.screens.photo

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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import us.mikeandwan.photos.ui.controls.photodetail.PhotoDetailBottomSheet

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PhotoScreen(
    viewModel: PhotoViewModel
) {
    val activePhotoIndex = viewModel.activePhotoIndex.collectAsState()
    val category = viewModel.activeCategory.collectAsState()
    val photos = viewModel.photos.collectAsState()
    val pagerState = rememberPagerState(pageCount = { photos.value.size })
    val showPositionAndCount = viewModel.showPosition.collectAsState()
    val showYearAndCategory = viewModel.showYearAndCategory.collectAsState()
    val isSlideshowPlaying = viewModel.playSlideshow.collectAsState()
    val showDetails = viewModel.showDetails.collectAsState()

    HorizontalPager(
        state = pagerState,
        beyondBoundsPageCount = 2,
        contentPadding = PaddingValues(0.dp),
        userScrollEnabled = true,
        pageContent = {
            AsyncImage(
                model = photos.value[it].mdUrl,
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
        if(showYearAndCategory.value || showPositionAndCount.value) {
            Row(modifier = Modifier.fillMaxWidth()) {
                if(showYearAndCategory.value && category.value != null) {
                    OverlayYearName(
                        category = category.value!!,
                        onClickYear = { year -> viewModel.navigateToYear(year) },
                        onClickCategory = { category -> viewModel.navigateToCategory(category) })
                }

                if(showPositionAndCount.value) {
                    OverlayPositionCount(
                        position = activePhotoIndex.value + 1,
                        count = photos.value.size
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
                isSlideshowPlaying = isSlideshowPlaying.value,
                onRotateLeft = { viewModel.rotatePhoto(-1) },
                onRotateRight = { viewModel.rotatePhoto(1) },
                onToggleSlideshow = { viewModel.toggleSlideshow() },
                onShare = { viewModel.sharePhoto() },
                onViewDetails = { viewModel.toggleDetails() }
            )
        }
    }

    if(showDetails.value) {
        PhotoDetailBottomSheet(
            onDismissRequest = { viewModel.toggleDetails() }
        )
    }
}

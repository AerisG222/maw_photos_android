package us.mikeandwan.photos.ui.controls.photodetail

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import us.mikeandwan.photos.R
import us.mikeandwan.photos.ui.controls.photocomment.PhotoCommentScreen
import us.mikeandwan.photos.ui.controls.photoexif.PhotoExifScreen
import us.mikeandwan.photos.ui.controls.photorating.PhotoRatingScreen

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PhotoDetailTabs() {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()

    val IDX_RATING = 0
    val IDX_COMMENT = 1
    val IDX_EXIF = 2

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            Tab(
                selected = pagerState.currentPage == IDX_RATING,
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(IDX_RATING)
                    }
                },
                icon = {
                    AsyncImage(
                        model = R.drawable.ic_star,
                        contentDescription = "Ratings",
                        modifier = Modifier.size(32.dp)
                    )
                },
            )
            Tab(
                selected = pagerState.currentPage == IDX_COMMENT,
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(IDX_COMMENT)
                    }
                },
                icon = {
                    AsyncImage(
                        model = R.drawable.ic_comment_white,
                        contentDescription = "Comment",
                        modifier = Modifier.size(32.dp)
                    )
                }
            )
            Tab(
                selected = pagerState.currentPage == IDX_EXIF,
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(IDX_EXIF)
                    }
                },
                icon = {
                    AsyncImage(
                        model = R.drawable.ic_tune,
                        contentDescription = "Exif",
                        modifier = Modifier.size(32.dp)
                    )
                }
            )
        }

        HorizontalPager(
            state = pagerState,
            userScrollEnabled = false,
            pageContent = {
                if (it == IDX_RATING) {
                    PhotoRatingScreen()
                }
                if (it == IDX_COMMENT) {
                    PhotoCommentScreen()
                }
                if (it == IDX_EXIF) {
                    PhotoExifScreen()
                }
            }
        )
    }
}

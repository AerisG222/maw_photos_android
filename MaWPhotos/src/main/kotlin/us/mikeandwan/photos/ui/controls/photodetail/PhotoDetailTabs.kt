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

private object TabIndex {
    const val RATING = 0
    const val COMMENT = 1
    const val EXIF = 2
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PhotoDetailTabs() {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            Tab(
                selected = pagerState.currentPage == TabIndex.RATING,
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(TabIndex.RATING)
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
                selected = pagerState.currentPage == TabIndex.COMMENT,
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(TabIndex.COMMENT)
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
                selected = pagerState.currentPage == TabIndex.EXIF,
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(TabIndex.EXIF)
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
                when(it) {
                    TabIndex.RATING -> PhotoRatingScreen()
                    TabIndex.COMMENT -> PhotoCommentScreen()
                    TabIndex.EXIF -> PhotoExifScreen()
                }
            }
        )
    }
}

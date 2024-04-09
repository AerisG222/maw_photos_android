package us.mikeandwan.photos.ui.controls.photodetail

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import us.mikeandwan.photos.R
import us.mikeandwan.photos.ui.controls.photocomment.PhotoCommentScreen
import us.mikeandwan.photos.ui.controls.photoexif.PhotoExifScreen
import us.mikeandwan.photos.ui.controls.photorating.PhotoRatingScreen

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PhotoDetailTabs() {
    var state by remember { mutableStateOf(0) }
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()

    val IDX_RATING = 0
    val IDX_COMMENT = 1
    val IDX_EXIF = 2

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = state) {
            Tab(
                selected = state == IDX_RATING,
                onClick = {
                    state = IDX_RATING
                    coroutineScope.launch {
                        pagerState.scrollToPage(state)
                    }
                },
                icon = {
                    AsyncImage(
                        model = R.drawable.ic_star,
                        contentDescription = "Ratings"
                    )
                }
            )
            Tab(
                selected = state == IDX_COMMENT,
                onClick = {
                    state = IDX_COMMENT
                    coroutineScope.launch {
                        pagerState.scrollToPage(state)
                    }
                },
                icon = {
                    AsyncImage(
                        model = R.drawable.ic_comment_white,
                        contentDescription = "Comment"
                    )
                }
            )
            Tab(
                selected = state == IDX_EXIF,
                onClick = {
                    state = IDX_EXIF
                    coroutineScope.launch {
                        pagerState.scrollToPage(state)
                    }
                },
                icon = {
                    AsyncImage(
                        model = R.drawable.ic_tune,
                        contentDescription = "Exif"
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
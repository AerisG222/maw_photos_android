package us.mikeandwan.photos.ui.controls.metadata

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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import us.mikeandwan.photos.R
import us.mikeandwan.photos.domain.models.PhotoComment

private object TabIndex {
    const val RATING = 0
    const val COMMENT = 1
    const val EXIF = 2
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DetailTabs(
    activePhotoId: Int,
    userRating: Short,
    averageRating: Float,
    exif: List<Pair<String, String>>,
    comments: List<PhotoComment>,
    setRating: (Short) -> Unit,
    addComment: (String) -> Unit,
    fetchRatingDetails: () -> Unit,
    fetchExifDetails: () -> Unit,
    fetchCommentDetails: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()

    val (commentPhotoId, setCommentPhotoId) = remember { mutableIntStateOf(0) }
    val (ratingPhotoId, setRatingPhotoId) = remember { mutableIntStateOf(0) }
    val (exifPhotoId, setExifPhotoId) = remember { mutableIntStateOf(0) }

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
                    TabIndex.RATING -> {
                        if(activePhotoId != ratingPhotoId) {
                            setRatingPhotoId(activePhotoId)
                            fetchRatingDetails()
                        }

                        RatingScreen(
                            userRating,
                            averageRating,
                            setRating
                        )
                    }
                    TabIndex.COMMENT -> {
                        if(activePhotoId != commentPhotoId) {
                            setCommentPhotoId(activePhotoId)
                            fetchCommentDetails()
                        }

                        CommentScreen(
                            comments,
                            addComment
                        )
                    }
                    TabIndex.EXIF -> {
                        if(activePhotoId != exifPhotoId) {
                            setExifPhotoId(activePhotoId)
                            fetchExifDetails()
                        }

                        ExifScreen(exif)
                    }
                }
            }
        )
    }
}

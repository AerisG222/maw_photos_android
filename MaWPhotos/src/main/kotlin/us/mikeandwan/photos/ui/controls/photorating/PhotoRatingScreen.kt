package us.mikeandwan.photos.ui.controls.photorating

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.smarttoolfactory.ratingbar.RatingBar
import com.smarttoolfactory.ratingbar.model.GestureStrategy
import com.smarttoolfactory.ratingbar.model.RateChangeStrategy
import com.smarttoolfactory.ratingbar.model.RatingInterval
import kotlinx.coroutines.launch
import us.mikeandwan.photos.R

@Composable
fun PhotoRatingScreen(
    viewModel: PhotoRatingViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val userRating = viewModel.userRating.collectAsState()
    val avgRating = viewModel.averageRating.collectAsState()

    Column(modifier = Modifier.fillMaxHeight()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp, 16.dp, 12.dp, 0.dp)
        ) {
            Text(
                text = stringResource(id = R.string.frg_rating_your_rating),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            RatingBar(
                itemSize = 32.dp,
                space = 2.dp,
                rating = userRating.value.toFloat(),
                imageVectorEmpty = ImageVector.vectorResource(R.drawable.ic_baseline_star_outline),
                imageVectorFilled = ImageVector.vectorResource(R.drawable.ic_star),
                tintEmpty = MaterialTheme.colorScheme.primary,
                tintFilled = MaterialTheme.colorScheme.primary,
                rateChangeStrategy = RateChangeStrategy.InstantChange,
                ratingInterval = RatingInterval.Full,
            ) { rating ->
                coroutineScope.launch {
                    viewModel.setRating(rating.toInt().toShort())
                }
            }
        }

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp, 16.dp, 12.dp, 0.dp)
        ) {
            Text(
                text = stringResource(id = R.string.frg_rating_avg_rating),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            RatingBar(
                rating = avgRating.value,
                imageVectorEmpty = ImageVector.vectorResource(R.drawable.ic_baseline_star_outline),
                imageVectorFilled = ImageVector.vectorResource(R.drawable.ic_star),
                tintEmpty = MaterialTheme.colorScheme.primary,
                tintFilled = MaterialTheme.colorScheme.primary,
                gestureStrategy = GestureStrategy.None
            ) {

            }
        }
    }
}

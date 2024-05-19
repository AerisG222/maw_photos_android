package us.mikeandwan.photos.ui.controls.metadata

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.ratingbar.RatingBar
import com.smarttoolfactory.ratingbar.model.GestureStrategy
import com.smarttoolfactory.ratingbar.model.RateChangeStrategy
import com.smarttoolfactory.ratingbar.model.RatingInterval
import us.mikeandwan.photos.R

@Composable
fun RatingScreen(state: RatingState) {
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
                rating = state.userRating.toFloat(),
                imageVectorEmpty = ImageVector.vectorResource(R.drawable.ic_baseline_star_outline),
                imageVectorFilled = ImageVector.vectorResource(R.drawable.ic_star),
                tintEmpty = MaterialTheme.colorScheme.primary,
                tintFilled = MaterialTheme.colorScheme.primary,
                rateChangeStrategy = RateChangeStrategy.InstantChange,
                ratingInterval = RatingInterval.Full,
                onRatingChange = { rating -> state.updateUserRating(rating.toInt().toShort()) }
            )
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
                rating = state.averageRating,
                imageVectorEmpty = ImageVector.vectorResource(R.drawable.ic_baseline_star_outline),
                imageVectorFilled = ImageVector.vectorResource(R.drawable.ic_star),
                tintEmpty = MaterialTheme.colorScheme.primary,
                tintFilled = MaterialTheme.colorScheme.primary,
                gestureStrategy = GestureStrategy.None,
                onRatingChange = { }
            )
        }
    }
}

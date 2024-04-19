package us.mikeandwan.photos.ui.controls.randomnavmenu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import us.mikeandwan.photos.R

@Composable
fun RandomMenu(
    fetchRandomPhotos: (Int) -> Unit,
    clearRandomPhotos: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            TextButton(
                onClick = { fetchRandomPhotos(10) }
            ) {
                Text(text = stringResource(id = R.string.fetch_10_photos))
            }
        }

        HorizontalDivider(modifier = Modifier.padding(16.dp, 0.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            TextButton(
                onClick = { fetchRandomPhotos(20) }
            ) {
                Text(text = stringResource(id = R.string.fetch_20_photos))
            }
        }

        Spacer(Modifier.weight(1f))

        HorizontalDivider(modifier = Modifier.padding(16.dp, 24.dp, 16.dp, 8.dp))

        OutlinedButton(
            onClick = { clearRandomPhotos() },
            modifier = Modifier
                .padding(16.dp, 4.dp, 16.dp, 16.dp)
                .fillMaxWidth(),
        ) {
            Text(
                text = stringResource(id = R.string.clear_random_photos)
            )
        }
    }
}

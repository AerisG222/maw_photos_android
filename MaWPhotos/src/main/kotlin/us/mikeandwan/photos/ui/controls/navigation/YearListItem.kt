package us.mikeandwan.photos.ui.controls.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun YearListItem(
    year: Int,
    isActive: Boolean,
    onYearSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // https://stackoverflow.com/a/75062699
    val bgColor = when(isActive) {
        true -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
    }

    val textColor = when(isActive) {
        true -> MaterialTheme.colorScheme.onPrimary
        else -> MaterialTheme.colorScheme.onSurface
    }

    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .background(color = bgColor)
            .fillMaxWidth()
    ) {
        TextButton(
            onClick = { onYearSelected(year) },
            modifier = modifier.fillMaxWidth()
        ) {
            Text(
                text = year.toString(),
                color = textColor
            )
        }
    }
}

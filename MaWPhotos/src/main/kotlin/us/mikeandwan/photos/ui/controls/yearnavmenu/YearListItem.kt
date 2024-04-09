package us.mikeandwan.photos.ui.controls.yearnavmenu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun YearListItem(
    year: Int,
    isActive: Boolean,
    onYearSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        TextButton(
            onClick = { onYearSelected(year) },
            modifier = modifier.fillMaxWidth(),
            enabled = !isActive
        ) {
            Text(text = year.toString())
        }
    }
}
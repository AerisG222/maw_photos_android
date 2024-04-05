package us.mikeandwan.photos.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun MenuPreference(
    expanded: Boolean,
    labelStringId: Int,
    options: List<String>,
    selectedValue: String,
    onRequestOpen: () -> Unit,
    onSelect: (String) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onRequestOpen() }
    ) {
        Text(text = stringResource(id = labelStringId))
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { }
        ) {
            LazyColumn(
                modifier = Modifier
                    .height((40 * options.size).dp)
                    .width(300.dp)
            ) {
                items(options) {
                    DropdownMenuItem(
                        text = { Text(text = it) },
                        onClick = { onSelect(it) }
                    )
                }
            }
        }
    }
}

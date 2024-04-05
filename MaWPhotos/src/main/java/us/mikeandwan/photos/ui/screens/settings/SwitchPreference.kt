package us.mikeandwan.photos.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource

@Composable
fun SwitchPreference (
    labelStringId: Int,
    isChecked: Boolean,
    onChange: (Boolean) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = stringResource(id = labelStringId))
        Switch(
            checked = isChecked,
            onCheckedChange = { onChange(it) }
        )
    }
}

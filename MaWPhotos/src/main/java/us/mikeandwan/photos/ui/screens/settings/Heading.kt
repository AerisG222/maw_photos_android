package us.mikeandwan.photos.ui.screens.settings

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource

@Composable
fun Heading (
    stringId: Int
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(text = stringResource(id = stringId))
    }
}
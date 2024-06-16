package us.mikeandwan.photos.ui.controls.photopager

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import us.mikeandwan.photos.domain.models.MediaCategory

@Composable
fun OverlayYearName(
    category: MediaCategory,
    onClickYear: (Int) -> Unit,
    onClickCategory: (MediaCategory) -> Unit
) {
    Row(modifier = Modifier.padding(4.dp, 2.dp)) {
        Text(
            text = category.year.toString(),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.
                clickable { onClickYear(category.year) }
        )

        Text(text = " / ")

        Text(
            text = category.name,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.
                clickable { onClickCategory(category) }
        )
    }
}

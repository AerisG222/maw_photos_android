package us.mikeandwan.photos.ui.screens.photo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import us.mikeandwan.photos.domain.models.PhotoCategory

@Composable
fun OverlayYearName(
    category: PhotoCategory,
    onClickYear: (Int) -> Unit,
    onClickCategory: (PhotoCategory) -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = category.year.toString(),
            modifier = Modifier.
                clickable { onClickYear(category.year) }
        )

        Text(text = " / ")

        Text(
            text = category.name,
            modifier = Modifier.
                clickable { onClickCategory(category) }
        )
    }
}
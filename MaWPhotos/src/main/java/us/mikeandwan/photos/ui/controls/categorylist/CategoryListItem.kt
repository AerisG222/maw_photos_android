package us.mikeandwan.photos.ui.controls.categorylist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import us.mikeandwan.photos.domain.models.PhotoCategory

@Composable
fun CategoryListItem(
    item: CategoryWithYearVisibility,
    onSelectCategory: (PhotoCategory) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier
            .fillMaxWidth()
            .clickable {
                onSelectCategory(item.category)
            }
    ) {
        AsyncImage(
            model = item.category.teaserUrl,
            contentDescription = item.category.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .height(60.dp)
                .width(60.dp)
                .padding(2.dp)
        )

        if(item.doShowYear) {
            Text(
                style = MaterialTheme.typography.titleMedium,
                text = item.category.year.toString(),
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.CenterVertically)
            )
        }

        Text(
            style = MaterialTheme.typography.titleMedium,
            text = item.category.name,
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.CenterVertically)
        )
    }
}

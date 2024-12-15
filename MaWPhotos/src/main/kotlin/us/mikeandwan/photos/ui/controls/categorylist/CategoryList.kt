package us.mikeandwan.photos.ui.controls.categorylist

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import us.mikeandwan.photos.domain.models.MediaCategory

@Composable
fun CategoryList(
    categories: List<MediaCategory>,
    showYear: Boolean,
    onSelectCategory: (MediaCategory) -> Unit
) {
    LazyColumn(
        Modifier.fillMaxSize()
    ) {
        itemsIndexed(
            categories,
            key = { index, item -> item.id }
        ) { index, category ->
            CategoryListItem(
                category,
                showYear,
                onSelectCategory
            )

            if (index != categories.size - 1) {
                HorizontalDivider(color = MaterialTheme.colorScheme.inverseOnSurface)
            }
        }
    }
}

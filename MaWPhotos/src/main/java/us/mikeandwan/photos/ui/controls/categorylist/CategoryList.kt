package us.mikeandwan.photos.ui.controls.categorylist

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import us.mikeandwan.photos.domain.models.PhotoCategory

@Composable
fun CategoryList(
    viewModel: CategoryListViewModel,
    onSelectListItem: (PhotoCategory) -> Unit
) {
    val categories = viewModel.categories.collectAsState()

    LazyColumn(Modifier.fillMaxSize()) {
        itemsIndexed(categories.value) { index, category ->
            CategoryListItem(
                category,
                onSelectListItem
            )

            if (index != categories.value.size - 1) {
                HorizontalDivider()
            }
        }
    }
}

package us.mikeandwan.photos.ui.controls.yearnavmenu

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun YearListMenu(
    years: List<Int>,
    activeYear: Int,
    onYearSelected: (Int) -> Unit
) {
    LazyColumn(Modifier.fillMaxSize()) {
        itemsIndexed(years) { index, year ->
            YearListItem(
                year,
                year == activeYear,
                { x -> onYearSelected(x) }
            )

            if (index != years.size - 1) {
                HorizontalDivider()
            }
        }
    }
}
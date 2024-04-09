package us.mikeandwan.photos.ui.controls.yearnavmenu

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun YearListMenu(
    viewModel: YearsViewModel = hiltViewModel()
) {
    val yearsState = viewModel.years.collectAsState()
    val activeYear = viewModel.activeYear.collectAsState()

    LazyColumn(Modifier.fillMaxSize()) {
        itemsIndexed(yearsState.value) { index, year ->
            YearListItem(
                year,
                year == activeYear.value,
                { x -> viewModel.onYearSelected(x) }
            )

            if (index != yearsState.value.size - 1) {
                HorizontalDivider()
            }
        }
    }
}
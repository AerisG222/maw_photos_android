package us.mikeandwan.photos.ui.controls.yearnavmenu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import us.mikeandwan.photos.R

@Composable
fun YearListMenu(
    viewModel: YearsViewModel = viewModel()
) {
    val yearsState = viewModel.years.collectAsState()

    LazyColumn(Modifier
        .fillMaxSize()
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(id = R.string.fragment_year_menu_choose_year),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(8.dp),
                )
            }
            HorizontalDivider()
        }
        itemsIndexed(yearsState.value) { index, d ->
            YearListItem(d)
            if (index != yearsState.value.size - 1) {
                HorizontalDivider()
            }
        }
    }
}
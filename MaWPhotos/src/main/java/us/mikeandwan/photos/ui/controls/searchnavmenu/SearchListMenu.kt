package us.mikeandwan.photos.ui.controls.searchnavmenu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import us.mikeandwan.photos.R

@Composable
fun SearchListMenu(
    viewModel: SearchNavMenuViewModel = viewModel()
) {
    val termsState = viewModel.searchTerms.collectAsState()

    LazyColumn {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(id = R.string.recent_searches),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(8.dp),
                )
            }

            HorizontalDivider()
        }

        itemsIndexed(termsState.value) { index, term ->
            SearchListItem(
                term.term,
                { newTerm -> viewModel.onTermSelected(newTerm) }
            )

            if (index != termsState.value.size - 1) {
                HorizontalDivider()
            }
        }

        item {
            if(termsState.value.isNotEmpty()) {
                HorizontalDivider(modifier = Modifier.padding(0.dp, 24.dp, 0.dp, 8.dp))

                OutlinedButton(
                    onClick = { viewModel.clearHistory() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(id = R.string.clear_search_history)
                    )
                }
            }
        }
    }
}

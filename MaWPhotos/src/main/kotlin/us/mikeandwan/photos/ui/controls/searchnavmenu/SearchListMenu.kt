package us.mikeandwan.photos.ui.controls.searchnavmenu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import us.mikeandwan.photos.R
import us.mikeandwan.photos.domain.models.SearchHistory

@Composable
fun SearchListMenu(
    recentSearchTerms: List<SearchHistory>,
    onTermSelected: (String) -> Unit,
    onClearSearchHistory: () -> Unit
) {
    val termDividerModifier = Modifier.padding(16.dp, 0.dp)

    Column(modifier = Modifier.fillMaxSize()) {
        if(recentSearchTerms.isEmpty()) {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "No recent searches",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontStyle = FontStyle.Italic
                )
            }
        } else {
            LazyColumn {
                itemsIndexed(recentSearchTerms) { index, term ->
                    SearchListItem(
                        term.term,
                        { newTerm -> onTermSelected(newTerm) }
                    )

                    if (index != recentSearchTerms.size - 1) {
                        HorizontalDivider(modifier = termDividerModifier)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            HorizontalDivider(modifier = Modifier.padding(16.dp, 24.dp, 16.dp, 8.dp))

            OutlinedButton(
                onClick = { onClearSearchHistory() },
                modifier = Modifier
                    .padding(16.dp, 4.dp, 16.dp, 16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.clear_search_history)
                )
            }
        }
    }
}

package us.mikeandwan.photos.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import us.mikeandwan.photos.R

@Composable
fun MenuPreference(
    labelStringId: Int,
    options: List<String>,
    selectedValue: String,
    onSelect: (String) -> Unit
) {
    var display by remember { mutableStateOf(false) }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { display = !display }
    ) {
        Column {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(id = labelStringId),
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = selectedValue,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        if(display) {
            Dialog(onDismissRequest = { display = false }) {
                ElevatedCard(
                    modifier = Modifier
                        .height(400.dp)
                        .width(300.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp, 16.dp, 16.dp, 8.dp)
                    ) {
                        Column {
                            Text(
                                text = stringResource(id = labelStringId),
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp)
                            )

                            LazyColumn {
                                itemsIndexed(options) { index, option ->
                                    val isSelected = option == selectedValue

                                    Row(modifier = Modifier
                                        .fillMaxWidth()
                                        .background(color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.background)
                                        .padding(8.dp)
                                        .clickable {
                                            display = false
                                            onSelect(option)
                                        }
                                    ) {
                                        Text(text = option)
                                    }

                                    if (index != options.size - 1) {
                                        HorizontalDivider()
                                    }
                                }
                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            TextButton(onClick = { display = false }) {
                                Text(text = stringResource(id = R.string.cancel))
                            }
                        }
                    }
                }
            }
        }
    }
}

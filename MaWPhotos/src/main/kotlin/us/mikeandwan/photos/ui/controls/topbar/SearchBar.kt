package us.mikeandwan.photos.ui.controls.topbar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import us.mikeandwan.photos.R

@Composable
fun SearchBar(
    initialSearchTerm: String,
    onSearch: (String) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val (searchTerm, setSearchTerm)= remember{ mutableStateOf(initialSearchTerm) }

    LaunchedEffect(initialSearchTerm) {
        setSearchTerm(initialSearchTerm)
    }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = searchTerm,
            singleLine = true,
            onValueChange = { setSearchTerm(it) },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearch(searchTerm)
                    keyboardController?.hide()
                }
            ),
            modifier = Modifier
                .padding(0.dp, 0.dp, 8.dp, 0.dp)
                .weight(1f)
        )

        IconButton(
            modifier = Modifier.size(42.dp),
            onClick = {
                onSearch(searchTerm)
                keyboardController?.hide()
            }
        ) {
            AsyncImage(
                model = R.drawable.ic_search,
                contentDescription = stringResource(R.string.search_icon_description),
                alignment = Alignment.Center,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
            )
        }
    }
}

@Preview
@Composable
fun SearchBarPreview() {
    SearchBar(
        initialSearchTerm = "Search",
        onSearch = {}
    )
}

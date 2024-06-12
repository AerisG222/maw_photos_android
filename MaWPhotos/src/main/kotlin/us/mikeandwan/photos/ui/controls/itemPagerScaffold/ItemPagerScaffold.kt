package us.mikeandwan.photos.ui.controls.itemPagerScaffold

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ItemPagerScaffold(
    showDetails: Boolean = false,
    topLeftContent: @Composable() () -> Unit = {},
    topRightContent: @Composable() () -> Unit = {},
    bottomBarContent: @Composable() () -> Unit = {},
    detailSheetContent: @Composable() () -> Unit = {},
    content: @Composable() () -> Unit,
) {
    content()

    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            topLeftContent()
            Spacer(Modifier.weight(1f))
            topRightContent()
        }

        Row(modifier = Modifier
            .height(40.dp)
            .fillMaxWidth()
            .padding(2.dp, 4.dp)
        ) {
            bottomBarContent()
        }
    }

    if(showDetails) {
        detailSheetContent()
    }
}

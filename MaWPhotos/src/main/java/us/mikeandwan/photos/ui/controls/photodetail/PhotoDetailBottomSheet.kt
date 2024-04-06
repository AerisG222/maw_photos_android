package us.mikeandwan.photos.ui.controls.photodetail

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoDetailBottomSheet(
    onDismissRequest: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = { onDismissRequest() }) {
        PhotoDetailTabs()
    }
}

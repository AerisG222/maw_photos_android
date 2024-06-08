package us.mikeandwan.photos.ui.screens.about

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlinx.serialization.Serializable
import us.mikeandwan.photos.R
import us.mikeandwan.photos.domain.models.NavigationArea
import us.mikeandwan.photos.ui.controls.logo.Logo

@Serializable
object AboutRoute

fun NavGraphBuilder.aboutScreen(
    updateTopBar : (Boolean, Boolean, String) -> Unit,
    setNavArea: (NavigationArea) -> Unit
) {
    composable<AboutRoute> {
        val vm: AboutViewModel = hiltViewModel()
        val history by vm.history.collectAsStateWithLifecycle()

        LaunchedEffect(Unit) {
            updateTopBar(true, false, "About")
            setNavArea(NavigationArea.About)
        }

        AboutScreen(
            vm.version,
            history
        )
    }
}

@Composable
fun AboutScreen(
    version: String,
    history: String
) {
    val tangerine = FontFamily(Font(R.font.tangerine))
    val markdownStyle = MaterialTheme.typography.bodyMedium
        .merge(color = MaterialTheme.colorScheme.onSurface)

    Column {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Logo()
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "mikeandwan.us",
                fontSize = 42.sp,
                fontFamily = tangerine,
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Photos",
                fontSize = 42.sp,
                fontFamily = tangerine,
            )
        }
        Row(
            modifier = Modifier
                .padding(0.dp, 0.dp, 0.dp, 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = version
            )
        }

        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            MarkdownText(
                markdown = history,
                style = markdownStyle
            )
        }
    }
}

@Preview
@Composable
fun AboutScreenPreview() {
    AboutScreen(
        version = "v2.0",
        history = "hi"
    )
}

package us.mikeandwan.photos.ui.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import coil.compose.AsyncImage
import us.mikeandwan.photos.R
import us.mikeandwan.photos.authorization.AuthStatus
import us.mikeandwan.photos.domain.models.NavigationArea
import us.mikeandwan.photos.ui.controls.logo.Logo

const val LoginRoute = "login"

fun NavGraphBuilder.loginScreen(
    updateTopBar : (Boolean, Boolean, String) -> Unit,
    setNavArea: (NavigationArea) -> Unit,
    navigateToCategories: () -> Unit
) {
    composable(LoginRoute) {
        val vm: LoginViewModel = hiltViewModel()
        val authStatus by vm.authStatus.collectAsStateWithLifecycle()

        LaunchedEffect(Unit) {
            updateTopBar(false, true, "Login")
            setNavArea(NavigationArea.Login)
        }

        LoginScreen(
            authStatus,
            initiateAuthentication = { vm.initiateAuthentication() },
            navigateToCategories = navigateToCategories
        )
    }
}

fun NavController.navigateToLogin() {
    this.navigate(LoginRoute)
}

@Composable
fun LoginScreen(
    authStatus: AuthStatus,
    initiateAuthentication: () -> Unit,
    navigateToCategories: () -> Unit
) {
    val tangerine = FontFamily(Font(R.font.tangerine))

    // commenting the item below in case a user wants to remain logged out
    // if we call this and they have active session on authorization server,
    // it will automatically log them in....
    //    if(authStatus == AuthStatus.RequiresAuthorization) {
    //        initiateAuthentication()
    //    }

    LaunchedEffect(authStatus) {
        if(authStatus == AuthStatus.Authorized) {
            navigateToCategories()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
        ) {
            AsyncImage(
                model = R.drawable.banner,
                contentDescription = "MaW Photos Banner",
                contentScale = ContentScale.Crop
            )
        }

        Column {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 0.dp, 0.dp, 32.dp)
            ) {
                Logo()
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "mikeandwan.us",
                    fontSize = 72.sp,
                    fontFamily = tangerine,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Photos",
                    fontSize = 72.sp,
                    fontFamily = tangerine,
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 0.dp, 0.dp, 24.dp)
        ) {
            Button(
                onClick = { initiateAuthentication() }
            ) {
                Text(
                    text = stringResource(id = R.string.activity_login_login_button_text)
                )
            }
        }
    }
}

package us.mikeandwan.photos.ui.screens.splash

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.coroutines.delay
import us.mikeandwan.photos.authorization.AuthStatus

const val SplashRoute = "splash"

fun NavGraphBuilder.splashScreen(
    navigateToLogin: () -> Unit,
    navigateToCategories: (Int) -> Unit
) {
    composable(SplashRoute) {
        val vm: SplashViewModel = hiltViewModel()
        val authStatus = vm.authStatus.collectAsStateWithLifecycle()
        val year = vm.mostRecentYear.collectAsStateWithLifecycle()

        LaunchedEffect(Unit) {
            // TODO: remove this delay
            delay(300)

            if(authStatus.value == AuthStatus.Authorized && year.value != null && year.value!! > 0) {
                navigateToCategories(year.value!!)
            } else {
                navigateToLogin()
            }
        }

        SplashScreen()
    }
}

@Composable
fun SplashScreen() {
    Column(modifier = Modifier.fillMaxSize()) {

    }
}

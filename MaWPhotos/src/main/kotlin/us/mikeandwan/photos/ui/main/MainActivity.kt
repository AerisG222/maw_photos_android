package us.mikeandwan.photos.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import us.mikeandwan.photos.domain.models.NavigationArea
import us.mikeandwan.photos.ui.controls.navigation.NavigationRail
import us.mikeandwan.photos.ui.controls.topbar.TopBar
import us.mikeandwan.photos.ui.screens.about.AboutRoute
import us.mikeandwan.photos.ui.screens.about.aboutScreen
import us.mikeandwan.photos.ui.screens.categories.CategoriesRoute
import us.mikeandwan.photos.ui.screens.categories.categoriesScreen
import us.mikeandwan.photos.ui.screens.category.CategoryRoute
import us.mikeandwan.photos.ui.screens.category.categoryScreen
import us.mikeandwan.photos.ui.screens.categoryItem.CategoryItemRoute
import us.mikeandwan.photos.ui.screens.categoryItem.categoryItemScreen
import us.mikeandwan.photos.ui.screens.login.LoginRoute
import us.mikeandwan.photos.ui.screens.login.loginScreen
import us.mikeandwan.photos.ui.screens.random.RandomRoute
import us.mikeandwan.photos.ui.screens.random.randomScreen
import us.mikeandwan.photos.ui.screens.randomItem.RandomItemRoute
import us.mikeandwan.photos.ui.screens.randomItem.randomItemScreen
import us.mikeandwan.photos.ui.screens.search.SearchRoute
import us.mikeandwan.photos.ui.screens.search.searchScreen
import us.mikeandwan.photos.ui.screens.settings.SettingsRoute
import us.mikeandwan.photos.ui.screens.settings.settingsScreen
import us.mikeandwan.photos.ui.screens.upload.UploadRoute
import us.mikeandwan.photos.ui.screens.upload.uploadScreen
import us.mikeandwan.photos.ui.theme.AppTheme
import java.time.LocalDate

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activity = this

        requestWindowFeature(Window.FEATURE_NO_TITLE)

        setContent {
            val vm: MainViewModel = hiltViewModel<MainViewModel>()
            val navController = rememberNavController()

            val mostRecentYear by vm.mostRecentYear
                .filter { it != null }
                .map { it!! }
                .collectAsStateWithLifecycle(initialValue = LocalDate.now().year)

            LaunchedEffect(Unit) {
                handleIntent(activity.intent ?: Intent(), vm, navController)
            }

            AppTheme {
                NavHost(
                    navController = navController,
                    startDestination = CategoriesRoute(mostRecentYear)
                ) {
                    loginScreen(
                        updateTopBar = ::updateTopBar,
                        setNavArea = { setNavArea(it) },
                        navigateAfterLogin = { navController.popBackStack() }
                    )
                    aboutScreen(
                        updateTopBar = ::updateTopBar,
                        setNavArea = { setNavArea(it) }
                    )
                    categoriesScreen(
                        updateTopBar = ::updateTopBar,
                        setActiveYear = { setActiveYear(it) },
                        setNavArea = { setNavArea(it) },
                        onNavigateToCategory = { navController.navigate(CategoryRoute(it.type.name, it.id)) },
                        navigateToLogin = { navController.navigate(LoginRoute) },
                        navigateToCategories = { navController.navigate(CategoriesRoute(it)) }
                    )
                    categoryScreen(
                        updateTopBar = ::updateTopBar,
                        setNavArea = { setNavArea(it) },
                        navigateToMedia = {
                            navController.navigate(CategoryItemRoute(it.type.name, it.categoryId, it.id))
                        },
                        navigateToLogin = { navController.navigate(LoginRoute) },
                    )
                    categoryItemScreen(
                        updateTopBar = ::updateTopBar,
                        setNavArea = { setNavArea(it) },
                        navigateToLogin = { navController.navigate(LoginRoute) },
                    )
                    randomScreen(
                        updateTopBar = ::updateTopBar,
                        onNavigateToPhoto = { navController.navigate(RandomItemRoute(it)) },
                        setNavArea = { setNavArea(it) },
                        navigateToLogin = { navController.navigate(LoginRoute) },
                    )
                    randomItemScreen(
                        updateTopBar = ::updateTopBar,
                        setNavArea = { setNavArea(it) },
                        navigateToYear = { navController.navigate(CategoriesRoute(it)) },
                        navigateToCategory = { navController.navigate(CategoryRoute(it.type.name, it.id)) },
                        navigateToLogin = { navController.navigate(LoginRoute) },
                    )
                    searchScreen(
                        updateTopBar = ::updateTopBar,
                        updateInitialSearchTerm = { setTopBarInitialSearchTerm(it) },
                        onNavigateToCategory = { navController.navigate(CategoryRoute(it.type.name, it.id)) },
                        setNavArea = { setNavArea(it) },
                        navigateToLogin = { navController.navigate(LoginRoute) },
                    )
                    settingsScreen(
                        updateTopBar = ::updateTopBar,
                        onNavigateToLogin = { navController.navigate(LoginRoute) },
                        setNavArea = { setNavArea(it) },
                    )
                    uploadScreen(
                        updateTopBar = ::updateTopBar,
                        setNavArea = { setNavArea(it) },
                        navigateToLogin = { navController.navigate(LoginRoute) },
                    )
                }
            }
        }
    }

    private fun handleIntent(intent: Intent, vm: MainViewModel, navController: NavController) {
        if (intent.action != null) {
            when (intent.action) {
                Intent.ACTION_SEND -> {
                    vm.handleSendSingle(intent)
                    navController.navigate(UploadRoute)
                }
                Intent.ACTION_SEND_MULTIPLE -> {
                    vm.handleSendMultiple(intent)
                    navController.navigate(UploadRoute)
                }
            }
        } else {
            vm.handleAuthorizeCallback(intent)
        }
    }
}

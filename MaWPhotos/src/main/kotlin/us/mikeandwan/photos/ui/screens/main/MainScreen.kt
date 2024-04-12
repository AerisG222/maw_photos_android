package us.mikeandwan.photos.ui.screens.main

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import us.mikeandwan.photos.domain.models.NavigationArea
import us.mikeandwan.photos.ui.controls.navigationrail.NavigationRail
import us.mikeandwan.photos.ui.controls.topbar.TopBar
import us.mikeandwan.photos.ui.screens.about.aboutScreen
import us.mikeandwan.photos.ui.screens.about.navigateToAbout
import us.mikeandwan.photos.ui.screens.categories.CategoriesRoute
import us.mikeandwan.photos.ui.screens.categories.categoriesScreen
import us.mikeandwan.photos.ui.screens.categories.navigateToCategories
import us.mikeandwan.photos.ui.screens.category.categoryScreen
import us.mikeandwan.photos.ui.screens.category.navigateToCategory
import us.mikeandwan.photos.ui.screens.login.loginScreen
import us.mikeandwan.photos.ui.screens.login.navigateToLogin
import us.mikeandwan.photos.ui.screens.photo.navigateToPhoto
import us.mikeandwan.photos.ui.screens.photo.photoScreen
import us.mikeandwan.photos.ui.screens.random.navigateToRandom
import us.mikeandwan.photos.ui.screens.random.randomScreen
import us.mikeandwan.photos.ui.screens.search.navigateToSearch
import us.mikeandwan.photos.ui.screens.search.searchScreen
import us.mikeandwan.photos.ui.screens.settings.navigateToSettings
import us.mikeandwan.photos.ui.screens.settings.settingsScreen
import us.mikeandwan.photos.ui.screens.upload.navigateToUpload
import us.mikeandwan.photos.ui.screens.upload.uploadScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val vm: MainViewModel = hiltViewModel()
    val coroutineScope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val snackbarHostState = remember { SnackbarHostState() }
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val context = LocalContext.current
    val activity = context.findActivity()

    LaunchedEffect(Unit) {
        handleIntent(activity?.intent ?: Intent(), vm, navController)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                NavigationRail(
                    activeArea = NavigationArea.Category,
                    navigateToCategories = {
                        navController.navigateToCategories()
                        coroutineScope.launch { drawerState.close() }
                    },
                    navigateToRandom = {
                        navController.navigateToRandom()
                        coroutineScope.launch { drawerState.close() }
                    },
                    navigateToSearch = {
                        navController.navigateToSearch()
                        coroutineScope.launch { drawerState.close() }
                    },
                    navigateToSettings = {
                        navController.navigateToSettings()
                        coroutineScope.launch { drawerState.close() }
                    },
                    navigateToUpload = {
                        navController.navigateToUpload()
                        coroutineScope.launch { drawerState.close() }
                    },
                    navigateToAbout = {
                        navController.navigateToAbout()
                        coroutineScope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopBar(
                    scrollBehavior,
                    onExpandNavMenu = { coroutineScope.launch { drawerState.open() } },
                )
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
        ) { innerPadding ->
            NavHost(
                modifier = Modifier.padding(innerPadding),
                navController = navController,
                startDestination = CategoriesRoute
            ) {
                loginScreen()
                aboutScreen()
                categoriesScreen(
                    onNavigateToCategory = { navController.navigateToCategory(it.id) }
                )
                categoryScreen(
                    onNavigateToPhoto = { navController.navigateToPhoto(it) }
                )
                photoScreen()
                randomScreen(
                    onNavigateToPhoto = { navController.navigateToPhoto(it) }
                )
                searchScreen(
                    onNavigateToCategory = { navController.navigateToCategory(it.id) }
                )
                settingsScreen(
                    onNavigateToLogin = { navController.navigateToLogin() }
                )
                uploadScreen()
            }
        }
    }
}

fun handleIntent(intent: Intent, vm: MainViewModel, navController: NavController) {
    if (intent.action != null) {
        when (intent.action) {
            Intent.ACTION_SEND -> {
                vm.handleSendSingle(intent)
                navController.navigateToUpload()
            }
            Intent.ACTION_SEND_MULTIPLE -> {
                vm.handleSendMultiple(intent)
                navController.navigateToUpload()
            }
        }
    } else {
        vm.handleAuthorizeCallback(intent)
    }
}

fun Context.findActivity(): ComponentActivity? = when (this) {
    is Activity -> this as ComponentActivity
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

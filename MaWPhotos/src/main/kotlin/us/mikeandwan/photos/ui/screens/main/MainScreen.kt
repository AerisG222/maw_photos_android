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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import us.mikeandwan.photos.domain.models.NavigationArea
import us.mikeandwan.photos.ui.controls.navigationrail.NavigationRail
import us.mikeandwan.photos.ui.controls.topbar.TopBar
import us.mikeandwan.photos.ui.screens.about.aboutScreen
import us.mikeandwan.photos.ui.screens.about.navigateToAbout
import us.mikeandwan.photos.ui.screens.categories.categoriesScreen
import us.mikeandwan.photos.ui.screens.categories.navigateToCategories
import us.mikeandwan.photos.ui.screens.category.categoryScreen
import us.mikeandwan.photos.ui.screens.category.navigateToCategory
import us.mikeandwan.photos.ui.screens.login.loginScreen
import us.mikeandwan.photos.ui.screens.login.navigateToLogin
import us.mikeandwan.photos.ui.screens.random.navigateToRandom
import us.mikeandwan.photos.ui.screens.random.randomScreen
import us.mikeandwan.photos.ui.screens.search.navigateToSearch
import us.mikeandwan.photos.ui.screens.search.searchScreen
import us.mikeandwan.photos.ui.screens.settings.navigateToSettings
import us.mikeandwan.photos.ui.screens.settings.settingsScreen
import us.mikeandwan.photos.ui.screens.splash.SplashRoute
import us.mikeandwan.photos.ui.screens.splash.splashScreen
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

    val years = vm.years.collectAsStateWithLifecycle(initialValue = emptyList())

    val context = LocalContext.current
    val activity = context.findActivity()

    val navArea by navController.currentBackStackEntryFlow.map {
        if (it.destination.route == "login") NavigationArea.Login
        else NavigationArea.Category
    }.collectAsStateWithLifecycle(initialValue = NavigationArea.Category)

    val (topBarDoShow, setTopBarDoShow) = remember { mutableStateOf(true) }
    val (topBarTitle, setTopBarTitle) = remember { mutableStateOf("") }
    val (topBarShowAppIcon, setTopBarShowAppIcon) = remember { mutableStateOf(false) }
    val (activeYear, setActiveYear) = remember { mutableIntStateOf(years.value.firstOrNull() ?: 0) }

    LaunchedEffect(Unit) {
        handleIntent(activity?.intent ?: Intent(), vm, navController)
    }

    fun updateTopBar(show: Boolean, showAppIcon: Boolean, title: String) {
        setTopBarDoShow(show)
        setTopBarShowAppIcon(showAppIcon)
        setTopBarTitle(title)
    }

    val mostRecentYear by vm.mostRecentYear
        .filter { it != null }
        .collectAsStateWithLifecycle(initialValue = 2024)

    ModalNavigationDrawer(
        gesturesEnabled = navArea != NavigationArea.Login,
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                NavigationRail(
                    activeArea = NavigationArea.Category,
                    years = years.value,
                    activeYear = activeYear,
                    navigateToCategories = {
                        navController.navigateToCategories(mostRecentYear!!)
                        coroutineScope.launch { drawerState.close() }
                    },
                    navigateToCategoriesByYear = {
                        setActiveYear(it)
                        navController.navigateToCategories(it)
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
                if (topBarDoShow) {
                    TopBar(
                        scrollBehavior,
                        title = topBarTitle,
                        showAppIcon = topBarShowAppIcon,
                        onExpandNavMenu = { coroutineScope.launch { drawerState.open() } },
                        onBackClicked = { navController.popBackStack() },
                    )
                }
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
        ) { innerPadding ->
            NavHost(
                modifier = Modifier.padding(innerPadding),
                navController = navController,
                startDestination = SplashRoute
            ) {
                splashScreen(
                    navigateToLogin = { navController.navigateToLogin() },
                    navigateToCategories = {
                        setActiveYear(it)
                        navController.navigateToCategories(it)
                    }
                )
                loginScreen(
                    updateTopBar = ::updateTopBar
                )
                aboutScreen(
                    updateTopBar = ::updateTopBar
                )
                categoriesScreen(
                    updateTopBar = ::updateTopBar,
                    onNavigateToCategory = { navController.navigateToCategory(it.id) }
                )
                categoryScreen(
                    updateTopBar = ::updateTopBar,
                )
                randomScreen(
                    updateTopBar = ::updateTopBar,
                    onNavigateToPhoto = { navController.navigateToCategory(it) }
                )
                searchScreen(
                    updateTopBar = ::updateTopBar,
                    onNavigateToCategory = { navController.navigateToCategory(it.id) }
                )
                settingsScreen(
                    updateTopBar = ::updateTopBar,
                    onNavigateToLogin = { navController.navigateToLogin() }
                )
                uploadScreen(
                    updateTopBar = ::updateTopBar
                )
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

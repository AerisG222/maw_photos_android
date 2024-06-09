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
import us.mikeandwan.photos.ui.screens.splash.SplashRoute
import us.mikeandwan.photos.ui.screens.splash.splashScreen
import us.mikeandwan.photos.ui.screens.upload.UploadRoute
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

    val years by vm.years.collectAsStateWithLifecycle(initialValue = emptyList())
    val recentSearchTerms by vm.recentSearchTerms.collectAsStateWithLifecycle(initialValue = emptyList())

    val context = LocalContext.current
    val activity = context.findActivity()

    val (navArea, setNavArea) = remember { mutableStateOf(NavigationArea.Category) }
    val (topBarDoShow, setTopBarDoShow) = remember { mutableStateOf(true) }
    val (topBarTitle, setTopBarTitle) = remember { mutableStateOf("") }
    val (topBarInitialSearchTerm, setTopBarInitialSearchTerm) = remember { mutableStateOf("") }
    val (topBarShowAppIcon, setTopBarShowAppIcon) = remember { mutableStateOf(false) }
    val (activeYear, setActiveYear) = remember { mutableIntStateOf(years.firstOrNull() ?: 0) }

    LaunchedEffect(Unit) {
        handleIntent(activity?.intent ?: Intent(), vm, navController)
    }

    LaunchedEffect(Unit) {
        vm.errorsToDisplay.collect {
            snackbarHostState.showSnackbar(it.message)
        }
    }

    fun updateTopBar(show: Boolean, showAppIcon: Boolean, title: String) {
        setTopBarDoShow(show)
        setTopBarShowAppIcon(showAppIcon)
        setTopBarTitle(title)
    }

    val mostRecentYear by vm.mostRecentYear
        .filter { it != null }
        .map { it!! }
        .collectAsStateWithLifecycle(initialValue = 2024)

    ModalNavigationDrawer(
        gesturesEnabled = navArea != NavigationArea.Login,
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                NavigationRail(
                    activeArea = navArea,
                    years = years,
                    activeYear = activeYear,
                    recentSearchTerms = recentSearchTerms,
                    fetchRandomPhotos = {
                        vm.fetchRandomPhotos(it)
                        coroutineScope.launch { drawerState.close() }
                    },
                    clearRandomPhotos = {
                        vm.clearRandomPhotos()
                        coroutineScope.launch { drawerState.close() }
                    },
                    navigateToCategories = {
                        navController.navigate(CategoriesRoute(mostRecentYear))
                        coroutineScope.launch { drawerState.close() }
                    },
                    navigateToCategoriesByYear = {
                        setActiveYear(it)
                        navController.navigate(CategoriesRoute(it))
                        coroutineScope.launch { drawerState.close() }
                    },
                    navigateToRandom = {
                        navController.navigate(RandomRoute)
                        coroutineScope.launch { drawerState.close() }
                    },
                    navigateToSearch = {
                        navController.navigate(SearchRoute())
                        coroutineScope.launch { drawerState.close() }
                    },
                    navigateToSearchWithTerm = {
                        navController.navigate(SearchRoute(it))
                        coroutineScope.launch { drawerState.close() }
                    },
                    onClearSearchHistory = {
                        vm.clearSearchHistory()
                    },
                    navigateToSettings = {
                        navController.navigate(SettingsRoute)
                        coroutineScope.launch { drawerState.close() }
                    },
                    navigateToUpload = {
                        navController.navigate(UploadRoute)
                        coroutineScope.launch { drawerState.close() }
                    },
                    navigateToAbout = {
                        navController.navigate(AboutRoute)
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
                        initialSearchTerm = topBarInitialSearchTerm,
                        showSearch = navArea == NavigationArea.Search,
                        title = topBarTitle,
                        showAppIcon = topBarShowAppIcon,
                        onExpandNavMenu = { coroutineScope.launch { drawerState.open() } },
                        onBackClicked = { navController.popBackStack() },
                        onSearch = { navController.navigate(SearchRoute(it)) },
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
                    navigateToLogin = { navController.navigate(LoginRoute) },
                    navigateToCategories = {
                        setActiveYear(it)
                        navController.navigate(CategoriesRoute(it))
                    }
                )
                loginScreen(
                    updateTopBar = ::updateTopBar,
                    setNavArea = { setNavArea(it) },
                    navigateToCategories = { navController.navigate(CategoriesRoute(mostRecentYear)) }
                )
                aboutScreen(
                    updateTopBar = ::updateTopBar,
                    setNavArea = { setNavArea(it) }
                )
                categoriesScreen(
                    updateTopBar = ::updateTopBar,
                    setNavArea = { setNavArea(it) },
                    onNavigateToCategory = { navController.navigate(CategoryRoute(it.id)) }
                )
                categoryScreen(
                    updateTopBar = ::updateTopBar,
                    setNavArea = { setNavArea(it) },
                    navigateToCategoryPhoto = { categoryId, photoId ->
                        navController.navigate(CategoryItemRoute(categoryId, photoId))
                    }
                )
                categoryItemScreen(
                    updateTopBar = ::updateTopBar,
                    setNavArea = { setNavArea(it) }
                )
                randomScreen(
                    updateTopBar = ::updateTopBar,
                    onNavigateToPhoto = { navController.navigate(RandomItemRoute(it)) },
                    setNavArea = { setNavArea(it) },
                )
                randomItemScreen(
                    updateTopBar = ::updateTopBar,
                    setNavArea = { setNavArea(it) },
                    navigateToYear = { navController.navigate(CategoriesRoute(it)) },
                    navigateToCategory = { navController.navigate(CategoryRoute(it.id)) }
                )
                searchScreen(
                    updateTopBar = ::updateTopBar,
                    updateInitialSearchTerm = { setTopBarInitialSearchTerm(it) },
                    onNavigateToCategory = { navController.navigate(CategoryRoute(it.id)) },
                    setNavArea = { setNavArea(it) },
                )
                settingsScreen(
                    updateTopBar = ::updateTopBar,
                    onNavigateToLogin = { navController.navigate(LoginRoute) },
                    setNavArea = { setNavArea(it) },
                )
                uploadScreen(
                    updateTopBar = ::updateTopBar,
                    setNavArea = { setNavArea(it) },
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

fun Context.findActivity(): ComponentActivity? = when (this) {
    is Activity -> this as ComponentActivity
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

package us.mikeandwan.photos.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import us.mikeandwan.photos.domain.models.NavigationArea
import us.mikeandwan.photos.ui.controls.navigation.NavigationRail
import us.mikeandwan.photos.ui.controls.topbar.TopBar
import us.mikeandwan.photos.ui.controls.topbar.TopBarState
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

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activity = this

        requestWindowFeature(Window.FEATURE_NO_TITLE)

        setContent {
            val vm = hiltViewModel<MainViewModel>()
            val navController = rememberNavController()

            val coroutineScope = rememberCoroutineScope()
            val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
            val snackbarHostState = remember { SnackbarHostState() }
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val years by vm.years.collectAsStateWithLifecycle(initialValue = emptyList())
            val recentSearchTerms by vm.recentSearchTerms.collectAsStateWithLifecycle(initialValue = emptyList())
            val navArea by vm.navArea.collectAsStateWithLifecycle()
            val topBarState by vm.topBarState.collectAsStateWithLifecycle()
            val activeYear by vm.activeYear.collectAsStateWithLifecycle()
            val mostRecentYear by vm.mostRecentYear.collectAsStateWithLifecycle()

            val enableDrawerGestures = remember(topBarState) { topBarState.show && topBarState.showAppIcon }

            LaunchedEffect(Unit) {
                handleIntent(activity.intent ?: Intent(), vm, navController)

                vm.errorsToDisplay.collect {
                    snackbarHostState.showSnackbar(it.message)
                }
            }

            fun updateTopBar(nextState: TopBarState) {
                vm.updateTopBar(nextState)
            }

            fun setNavArea(area: NavigationArea) {
                vm.setNavArea(area)
            }

            fun setActiveYear(year: Int) {
                vm.setActiveYear(year)
            }

            AppTheme {
                ModalNavigationDrawer(
                    gesturesEnabled = enableDrawerGestures,
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet(
                            drawerContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        ) {
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
                                clearSearchHistory = {
                                    vm.clearSearchHistory()
                                },
                                navigateToCategories = {
                                    navController.navigate(CategoriesRoute(mostRecentYear))
                                    coroutineScope.launch { drawerState.close() }
                                },
                                navigateToCategoriesByYear = {
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
                            if (topBarState.show) {
                                TopBar(
                                    scrollBehavior,
                                    state = topBarState,
                                    onExpandNavMenu = { coroutineScope.launch { drawerState.open() } },
                                    onBackClicked = { navController.navigateUp() },
                                    onSearch = { navController.navigate(SearchRoute(it)) },
                                )
                            }
                        },
                        snackbarHost = {
                            SnackbarHost(hostState = snackbarHostState)
                        },
                    ) { innerPadding ->
                        // https://issuetracker.google.com/issues/297824100
                        Column(modifier = Modifier.fillMaxSize()) {
                            NavHost(
                                modifier = Modifier.padding(innerPadding),
                                navController = navController,
                                startDestination = CategoriesRoute(mostRecentYear)
                            ) {
                                loginScreen(
                                    updateTopBar = ::updateTopBar,
                                    setNavArea = ::setNavArea,
                                    navigateAfterLogin = { navController.navigateUp() }
                                )
                                aboutScreen(
                                    updateTopBar = ::updateTopBar,
                                    setNavArea = ::setNavArea
                                )
                                categoriesScreen(
                                    updateTopBar = ::updateTopBar,
                                    setNavArea = ::setNavArea,
                                    setActiveYear = ::setActiveYear,
                                    navigateToCategory = {
                                        navController.navigate(
                                            CategoryRoute(
                                                it.type.name,
                                                it.id
                                            )
                                        )
                                    },
                                    navigateToLogin = { navController.navigate(LoginRoute) },
                                    navigateToCategories = {
                                        navController.navigate(
                                            CategoriesRoute(
                                                it
                                            )
                                        )
                                    }
                                )
                                categoryScreen(
                                    updateTopBar = ::updateTopBar,
                                    setNavArea = ::setNavArea,
                                    navigateToMedia = {
                                        navController.navigate(
                                            CategoryItemRoute(
                                                it.type.name,
                                                it.categoryId,
                                                it.id
                                            )
                                        )
                                    },
                                    navigateToLogin = { navController.navigate(LoginRoute) },
                                )
                                categoryItemScreen(
                                    updateTopBar = ::updateTopBar,
                                    setNavArea = ::setNavArea,
                                    navigateToLogin = { navController.navigate(LoginRoute) },
                                )
                                randomScreen(
                                    updateTopBar = ::updateTopBar,
                                    setNavArea = ::setNavArea,
                                    navigateToPhoto = { navController.navigate(RandomItemRoute(it)) },
                                    navigateToLogin = { navController.navigate(LoginRoute) },
                                )
                                randomItemScreen(
                                    updateTopBar = ::updateTopBar,
                                    setNavArea = ::setNavArea,
                                    navigateToYear = { navController.navigate(CategoriesRoute(it)) },
                                    navigateToCategory = {
                                        navController.navigate(
                                            CategoryRoute(
                                                it.type.name,
                                                it.id
                                            )
                                        )
                                    },
                                    navigateToLogin = { navController.navigate(LoginRoute) },
                                )
                                searchScreen(
                                    updateTopBar = ::updateTopBar,
                                    setNavArea = ::setNavArea,
                                    navigateToCategory = {
                                        navController.navigate(
                                            CategoryRoute(
                                                it.type.name,
                                                it.id
                                            )
                                        )
                                    },
                                    navigateToLogin = { navController.navigate(LoginRoute) },
                                )
                                settingsScreen(
                                    updateTopBar = ::updateTopBar,
                                    setNavArea = ::setNavArea,
                                    navigateToLogin = { navController.navigate(LoginRoute) },
                                )
                                uploadScreen(
                                    updateTopBar = ::updateTopBar,
                                    setNavArea = ::setNavArea,
                                    navigateToLogin = { navController.navigate(LoginRoute) },
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun handleIntent(intent: Intent, vm: MainViewModel, navController: NavController) {
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
    }
}

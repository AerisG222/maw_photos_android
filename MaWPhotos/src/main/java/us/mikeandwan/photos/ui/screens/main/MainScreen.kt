package us.mikeandwan.photos.ui.screens.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import us.mikeandwan.photos.ui.controls.navigationrail.NavigationRail
import us.mikeandwan.photos.ui.controls.topbar.TopBar
import us.mikeandwan.photos.ui.screens.about.aboutScreen
import us.mikeandwan.photos.ui.screens.categories.CategoriesRoute
import us.mikeandwan.photos.ui.screens.categories.categoriesScreen
import us.mikeandwan.photos.ui.screens.category.categoryScreen
import us.mikeandwan.photos.ui.screens.category.navigateToCategory
import us.mikeandwan.photos.ui.screens.photo.navigateToPhoto
import us.mikeandwan.photos.ui.screens.photo.photoScreen
import us.mikeandwan.photos.ui.screens.random.randomScreen
import us.mikeandwan.photos.ui.screens.search.searchScreen
import us.mikeandwan.photos.ui.screens.settings.settingsScreen
import us.mikeandwan.photos.ui.screens.upload.uploadScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val snackbarHostState = remember { SnackbarHostState() }
    val navController = rememberNavController()

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                NavigationRail()
            }
        }
    ) {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopBar(scrollBehavior)
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
                settingsScreen()
                uploadScreen()
            }
        }
    }
}

package us.mikeandwan.photos.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import us.mikeandwan.photos.ui.screens.main.MainScreen
import us.mikeandwan.photos.ui.theme.AppTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                MainScreen()
            }
        }
    }

//    private fun initStateObservers() {
//        navController.addOnDestinationChangedListener { controller, destination, bundle ->
//            viewModel.destinationChanged(destination.id)
//        }
//
//        lifecycleScope.launch {
//            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                viewModel.authStatus
//                    .filter { it is AuthStatus.RequiresAuthorization }
//                    .onEach { goToLoginScreen() }
//                    .launchIn(this)
//
//                viewModel.shouldCloseDrawer
//                    .filter { it }
//                    .onEach {
//                        binding.drawerLayout.closeDrawer(binding.navLayout)
//                        viewModel.drawerClosed()
//                    }
//                    .launchIn(this)
//
//                viewModel.shouldOpenDrawer
//                    .filter { it }
//                    .onEach {
//                        binding.drawerLayout.openDrawer(binding.navLayout)
//                        viewModel.drawerOpened()
//                    }
//                    .launchIn(this)
//
//                viewModel.shouldNavigateBack
//                    .filter { it }
//                    .onEach {
//                        navController.navigateUp()
//                        viewModel.navigationBackCompleted()
//                    }
//                    .launchIn(this)
//
//                viewModel.enableDrawer
//                    .map { if(it) DrawerLayout.LOCK_MODE_UNLOCKED else DrawerLayout.LOCK_MODE_LOCKED_CLOSED }
//                    .onEach { binding.drawerLayout.setDrawerLockMode(it) }
//                    .launchIn(this)
//
//                viewModel.navigationRequests
//                    .filter { it.actionId != null || it.targetNavigationArea == NavigationArea.Login }
//                    .onEach { onNavigate(it) }
//                    .launchIn(this)
//
//                viewModel.navigationArea
//                    .onEach { updateSubnav(it) }
//                    .launchIn(this)
//
//                viewModel.displayError
//                    .filter { it is ErrorMessage.Display }
//                    .map { it as ErrorMessage.Display }
//                    .onEach { showError(it.message) }
//                    .launchIn(this)
//
//                viewModel.clearFileCache()
//            }
//        }
//    }
//
//    private fun showError(message: String) {
//        viewModel.errorDisplayed()
//
//        val snackbar = Snackbar.make(this, binding.root, message, Snackbar.LENGTH_SHORT)
//
//        snackbar.show()
//    }
//
//    private fun onNavigate(instruction: NavigationInstruction) {
//        viewModel.navigationRequestCompleted()
//
//        if(instruction.targetNavigationArea == NavigationArea.Login) {
//            goToLoginScreen()
//        } else {
//            if(instruction.popBackId != null) {
//                navController.popBackStack(instruction.popBackId, false)
//            }
//
//            navController.navigate(instruction.actionId!!)
//
//            viewModel.requestNavDrawerClose()
//        }
//    }
//
//    private fun updateSubnav(area: NavigationArea) {
//        val frag = when(area) {
//            NavigationArea.Category -> YearsFragment::class.java
//            NavigationArea.Random -> RandomMenuFragment::class.java
//            NavigationArea.Search -> SearchNavMenuFragment::class.java
//            else -> null
//        } ?: return
//
//        supportFragmentManager.commit {
//            setReorderingAllowed(true)
//            replace(R.id.fragmentSubnav, frag, null)
//        }
//    }
//
//    private fun goToLoginScreen() {
//        val intent = Intent(this, LoginActivity::class.java)
//        startActivity(intent)
//        finish()
//    }
}
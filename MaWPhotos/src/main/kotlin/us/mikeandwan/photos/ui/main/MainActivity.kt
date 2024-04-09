package us.mikeandwan.photos.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import dagger.hilt.android.AndroidEntryPoint

import us.mikeandwan.photos.ui.screens.main.MainScreen
import us.mikeandwan.photos.ui.theme.AppTheme

@Suppress("UNUSED_ANONYMOUS_PARAMETER")
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
//
//    private fun initShareReceiver() {
//        val intent = intent
//        val action = intent.action
//
//        if (action != null) {
//            when (action) {
//                Intent.ACTION_SEND -> handleSendSingle(intent)
//                Intent.ACTION_SEND_MULTIPLE -> handleSendMultiple(intent)
//            }
//        }
//    }
//
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
////        supportFragmentManager.commit {
////            setReorderingAllowed(true)
////            replace(R.id.fragmentSubnav, frag, null)
////        }
//    }
//
//    private fun goToLoginScreen() {
//        val intent = Intent(this, LoginActivity::class.java)
//        startActivity(intent)
//        finish()
//    }
//
//    private fun handleSendSingle(intent: Intent) {
//        val mediaUri = if (Build.VERSION.SDK_INT >= 33) {
//            intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
//        } else {
//            @Suppress("DEPRECATION")
//            intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
//        }
//
//        if(mediaUri != null) {
//            enqueueUpload(mediaUri)
//        }
//    }
//
//    private fun handleSendMultiple(intent: Intent) {
//        val mediaUris = if (Build.VERSION.SDK_INT >= 33) {
//            intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM, Uri::class.java)
//        } else {
//            @Suppress("DEPRECATION")
//            intent.getParcelableArrayListExtra<Uri?>(Intent.EXTRA_STREAM)
//        }
//
//        if(mediaUris != null) {
//            enqueueUpload(*mediaUris.toTypedArray())
//        }
//    }
//
//    private fun enqueueUpload(vararg mediaUri: Uri) {
//        lifecycleScope.launch {
//            mediaUri.forEach {
//                val file = viewModel.saveUploadFile(it)
//
//                if (file != null) {
//                    val constraints = Constraints.Builder()
//                        .setRequiredNetworkType(NetworkType.UNMETERED)
//                        .build()
//
//                    val data = workDataOf(
//                        UploadWorker.KEY_FILENAME to file.path
//                    )
//
//                    val work = OneTimeWorkRequestBuilder<UploadWorker>()
//                        .setBackoffCriteria(
//                            BackoffPolicy.EXPONENTIAL,
//                            1,
//                            TimeUnit.MINUTES
//                        )
//                        .setConstraints(constraints)
//                        .setInputData(data)
//                        .build()
//
//                    val workManager = WorkManager.getInstance(applicationContext)
//
//                    workManager.enqueueUniqueWork(
//                        "upload ${file.path}",
//                        ExistingWorkPolicy.REPLACE,
//                        work
//                    )
//                }
//            }
//
//            val action = MobileNavigationDirections.actionNavigateToUpload()
//
//            navController.navigate(action)
//        }
//    }
}
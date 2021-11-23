package us.mikeandwan.photos.ui.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.work.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import us.mikeandwan.photos.MobileNavigationDirections
import us.mikeandwan.photos.R
import us.mikeandwan.photos.databinding.ActivityMainBinding
import us.mikeandwan.photos.domain.models.NavigationArea
import us.mikeandwan.photos.domain.models.NavigationInstruction
import us.mikeandwan.photos.ui.controls.randomnavmenu.RandomMenuFragment
import us.mikeandwan.photos.ui.controls.yearnavmenu.YearsFragment
import us.mikeandwan.photos.ui.login.LoginActivity
import us.mikeandwan.photos.workers.UploadWorker

@Suppress("UNUSED_ANONYMOUS_PARAMETER")
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        setContentView(binding.root)

        initStateObservers()

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
        navController = navHostFragment.navController

        binding.appIconImage.setOnClickListener {
            if(!binding.drawerLayout.isDrawerOpen(binding.navLayout)) {
                binding.drawerLayout.openDrawer(binding.navLayout, true)
            }
        }

        binding.appIconBack.setOnClickListener {
            navController.navigateUp()
        }

        navController.addOnDestinationChangedListener { controller, destination, bundle ->
            viewModel.destinationChanged(destination.id)
        }

        initShareReceiver()
    }

    private fun initShareReceiver() {
        val intent = intent
        val action = intent.action

        if (action != null) {
            when (action) {
                Intent.ACTION_SEND -> handleSendSingle(intent)
                Intent.ACTION_SEND_MULTIPLE -> handleSendMultiple(intent)
            }
        }
    }

    private fun initStateObservers() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // DEV: uncomment to force reauth
                // viewModel.authService.clearAuthState()

                viewModel.isAuthenticated
                    .onEach {
                        if (!it) {
                            goToLoginScreen()
                        }
                    }
                    .launchIn(this)

                viewModel.shouldCloseDrawer
                    .onEach { doClose ->
                        if (doClose) {
                            binding.drawerLayout.closeDrawer(binding.navLayout)
                            viewModel.drawerClosed()
                        }
                    }
                    .launchIn(this)

                viewModel.enableDrawer
                    .onEach { enableDrawer ->
                        if(enableDrawer) {
                            binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                        } else {
                            binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                        }
                    }
                    .launchIn(this)

                viewModel.navigationRequests
                    .filter { it.actionId != null }
                    .onEach { onNavigate(it) }
                    .launchIn(this)

                viewModel.clearFileCache()
            }
        }
    }

    private fun onNavigate(instruction: NavigationInstruction) {
        if(instruction.popBackId != null) {
            navController.popBackStack(instruction.popBackId, false)
        }

        navController.navigate(instruction.actionId!!)
        updateSubnav(instruction)

        viewModel.requestNavDrawerClose()
        viewModel.navigationRequestCompleted()
    }

    private fun updateSubnav(navigationInstruction: NavigationInstruction) {
        val frag = when(navigationInstruction.targetNavigationArea) {
            NavigationArea.Category -> YearsFragment::class.java
            NavigationArea.Random -> RandomMenuFragment::class.java
            else -> null
        } ?: return

        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.fragmentSubnav, frag, null)
        }
    }

    private fun goToLoginScreen() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun handleSendSingle(intent: Intent) {
        val mediaUri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)

        if(mediaUri != null) {
            enqueueUpload(mediaUri)
        }
    }

    private fun handleSendMultiple(intent: Intent) {
        val mediaUris = intent.getParcelableArrayListExtra<Uri?>(Intent.EXTRA_STREAM)

        if(mediaUris != null) {
            enqueueUpload(*mediaUris.toTypedArray())
        }
    }

    private fun enqueueUpload(vararg mediaUri: Uri) {
        lifecycleScope.launch {
            mediaUri.forEach {
                val file = viewModel.saveUploadFile(it)

                if (file != null) {
                    val constraints = Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.UNMETERED)
                        .build()

                    val data = workDataOf(
                        UploadWorker.KEY_FILENAME to file.path
                    )

                    val work = OneTimeWorkRequestBuilder<UploadWorker>()
                        .setConstraints(constraints)
                        .setInputData(data)
                        .build()

                    val workManager = WorkManager.getInstance(applicationContext)

                    workManager.enqueueUniqueWork(
                        "upload ${file.path}",
                        ExistingWorkPolicy.REPLACE,
                        work
                    )
                }
            }

            val action = MobileNavigationDirections.actionNavigateToUpload()

            navController.navigate(action)
        }
    }
}
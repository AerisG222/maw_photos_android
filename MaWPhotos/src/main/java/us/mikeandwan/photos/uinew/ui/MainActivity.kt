package us.mikeandwan.photos.uinew.ui

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import us.mikeandwan.photos.R
import us.mikeandwan.photos.databinding.ActivityMainBinding
import us.mikeandwan.photos.ui.login.LoginActivity
import us.mikeandwan.photos.uinew.ui.randomnavmenu.RandomMenuFragment
import us.mikeandwan.photos.uinew.ui.yearnavmenu.YearsFragment

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
                    .filter { it != null }
                    .onEach { onNavigate(it!!) }
                    .launchIn(this)
            }
        }
    }

    private fun onNavigate(id: Int) {
        if (id != navController.currentDestination?.id) {
            navController.navigate(id)
            updateSubnav(id)
        } else {
            viewModel.requestNavDrawerClose()
        }

        viewModel.navigationRequestCompleted()
    }

    private fun updateSubnav(id: Int) {
        val frag = when(id) {
            R.id.navigation_random -> RandomMenuFragment::class.java
            R.id.navigation_categories -> YearsFragment::class.java
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
}
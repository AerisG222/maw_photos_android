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
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import us.mikeandwan.photos.R
import us.mikeandwan.photos.databinding.ActivityMainBinding
import us.mikeandwan.photos.ui.login.LoginActivity
import us.mikeandwan.photos.uinew.ui.randomMenu.RandomMenuFragment
import us.mikeandwan.photos.uinew.ui.years.YearsFragment

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
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

        appBarConfiguration = AppBarConfiguration.Builder(
                R.id.navigation_categories,
                R.id.navigation_random
            )
            .setOpenableLayout(binding.drawerLayout)
            .build()

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

    override fun onSupportNavigateUp(): Boolean { //Setup appBarConfiguration for back arrow
        return NavigationUI.navigateUp(navController, appBarConfiguration)
    }

    private fun initStateObservers() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // DEV: uncomment to force reauth
                // viewModel.authService.clearAuthState()

                viewModel.isAuthenticated.collect {
                    if (!it) {
                        goToLoginScreen()
                    }
                }
            }
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.shouldCloseDrawer.collect{ doClose ->
                    if(doClose) {
                        binding.drawerLayout.closeDrawer(Gravity.START)
                        viewModel.drawerClosed()
                    }
                }
            }
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.enableDrawer.collect { enableDrawer ->
                    if(enableDrawer) {
                        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                    } else {
                        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                    }
                }
            }
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.navigationRequests.collect { onNavigate(it) }
            }
        }
    }

    private fun onNavigate(id: Int?) {
        if (id != null) {
            navController.navigate(id)
            updateSubnav(id)
            viewModel.navigationRequestCompleted()
        }
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
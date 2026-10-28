package com.example.qaphela

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.qaphela.databinding.ActivityMainBinding
import com.example.qaphela.ui.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.mainRoot) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        setSupportActionBar(binding.topToolbar)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.scanSmsFragment,
                R.id.checkLinkFragment,
                R.id.historyFragment,
                R.id.settingsFragment
            )
        )
        binding.topToolbar.setupWithNavController(navController, appBarConfiguration)

        // Custom BottomNav listener for instant responsive tab switching
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            if (navController.currentDestination?.id == item.itemId) {
                return@setOnItemSelectedListener true
            }

            val builder = NavOptions.Builder()
                .setLaunchSingleTop(true)
                .setRestoreState(true)
                .setPopUpTo(
                    R.id.homeFragment,
                    inclusive = false,
                    saveState = true
                )

            val options = builder.build()
            return@setOnItemSelectedListener try {
                navController.navigate(item.itemId, null, options)
                true
            } catch (e: Exception) {
                false
            }
        }

        binding.bottomNavigation.setOnItemReselectedListener {
            // Avoid reloading screen on reselect
        }

        // Synchronize selected tab with current destination
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.splashFragment, R.id.onboardingFragment, R.id.loginFragment, R.id.registerFragment -> {
                    binding.appBarLayout.visibility = View.GONE
                    binding.bottomNavigation.visibility = View.GONE
                }
                else -> {
                    binding.appBarLayout.visibility = View.VISIBLE
                    binding.bottomNavigation.visibility = View.VISIBLE
                    val menu = binding.bottomNavigation.menu
                    val item = menu.findItem(destination.id)
                    if (item != null) {
                        item.isChecked = true
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.getHelpFastFragment -> {
                navController.navigate(R.id.getHelpFastFragment)
                true
            }
            R.id.settingsFragment -> {
                navController.navigate(R.id.settingsFragment)
                true
            }
            R.id.chatbotFragment -> {
                navController.navigate(R.id.chatbotFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

package com.example.tmdbapplication.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.tmdbapplication.R
import com.example.tmdbapplication.databinding.ActivityMainBinding
import com.example.tmdbapplication.util.setVisibility
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupNavigation()
    }

    private fun setupNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.home_destination, R.id.watchlist_destination, R.id.search_destination)
        )

        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.movieListFragment -> {
                    binding.bottomNavigation.setVisibility(true)
                    binding.toolbar.setVisibility(false)
                }
                R.id.watchlistFragment -> {
                    binding.bottomNavigation.setVisibility(true)
                    binding.toolbar.setVisibility(false)
                }
                R.id.searchMovieFragment -> {
                    binding.bottomNavigation.setVisibility(true)
                    binding.toolbar.setVisibility(false)
                }
                else -> {
                    binding.bottomNavigation.setVisibility(false)
                    binding.toolbar.setVisibility(true)
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)
    }
}
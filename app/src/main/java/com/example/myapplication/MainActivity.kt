package com.example.myapplication

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)

        binding.toolbar.inflateMenu(R.menu.main_menu)
        
        val cartMenuItem = binding.toolbar.menu.findItem(R.id.cartFragment)
        val actionView = cartMenuItem?.actionView
        val cartBadge = actionView?.findViewById<TextView>(R.id.cartBadge)

        actionView?.setOnClickListener {
            navController.navigate(R.id.cartFragment)
        }

        viewModel.cartItems.observe(this) { items ->
            val count = items.sumOf { it.quantity }
            if (count > 0) {
                cartBadge?.visibility = View.VISIBLE
                cartBadge?.text = count.toString()
            } else {
                cartBadge?.visibility = View.GONE
            }
        }

        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.signOut -> {
                    viewModel.logout()
                    navController.navigate(R.id.loginFragment)
                    true
                }
                else -> {
                    NavigationUI.onNavDestinationSelected(item, navController)
                }
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.loginFragment || destination.id == R.id.createAccountFragment) {
                binding.appBarLayout.visibility = View.GONE
            } else {
                binding.appBarLayout.visibility = View.VISIBLE
                
                val isHome = destination.id == R.id.homeFragment
                val menu = binding.toolbar.menu
                for (i in 0 until menu.size()) {
                    val item = menu.getItem(i)
                    if (item.itemId != R.id.cartFragment) {
                        item.isVisible = isHome
                    }
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}

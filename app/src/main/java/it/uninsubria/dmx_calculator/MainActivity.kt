package it.uninsubria.dmx_calculator

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import it.uninsubria.dmx_calculator.databinding.ActivityMainBinding

@Suppress("unused")
private const val TAG = "Main_Activity"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var bottomNavView: BottomNavigationView
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bottomNavView = binding.BNVMainActivity
        navController = findNavController(R.id.HF_mainActivity)

        val navBarConfiguration = AppBarConfiguration(setOf(R.id.navigation_dip_switch,
                                                            R.id.navigation_list_fixture,
                                                            R.id.navigation_new_fixture,
                                                            R.id.navigation_list_project,
                                                            R.id.navigation_new_project))
        setupActionBarWithNavController(navController, navBarConfiguration)
        bottomNavView.setupWithNavController(navController)
    }
}
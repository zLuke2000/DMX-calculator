package it.uninsubria.dmx_calculator

import android.graphics.Color.GRAY
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    private val TAG = "Main Activity"

    private lateinit var bottomNavView: BottomNavigationView
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavView = findViewById(R.id.bottom_navigation_view)
        navController = findNavController(R.id.nav_host_fragment)

        val navBarConfiguration = AppBarConfiguration(setOf(R.id.navigation_dip_switch,
                                                            R.id.navigation_list_fixture,
                                                            R.id.navigation_new_fixture,
                                                            R.id.navigation_list_project,
                                                            R.id.navigation_new_project))
        setupActionBarWithNavController(navController, navBarConfiguration)
        bottomNavView.setupWithNavController(navController)
    }
}
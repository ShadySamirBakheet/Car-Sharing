package com.app.carsharing.views.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.app.carsharing.R
import com.app.carsharing.data.datasources.SharedStorage
import com.app.carsharing.databinding.ActivityHomeBinding
import com.app.carsharing.views.trips.MyTripsActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        if (SharedStorage.getHaveCarData(this)) {
            navView.inflateMenu(R.menu.bottom_nav_menu)
        } else {
            navView.inflateMenu(R.menu.user_menu)
        }

        val navController = findNavController(R.id.nav_host_fragment_activity_home)

        navView.setupWithNavController(navController)

        binding.myTrip.setOnClickListener {
            startActivity(Intent(this, MyTripsActivity::class.java))
        }

    }
}
package shady.samir.carsharing.views.home

import android.content.Intent
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity

import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import shady.samir.carsharing.R
import shady.samir.carsharing.databinding.ActivityHomeBinding
import shady.samir.carsharing.viewmodel.NetworkViewModel
import shady.samir.carsharing.views.trips.MyTripsActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_home)

        navView.setupWithNavController(navController)

        binding.myTrip.setOnClickListener {
            startActivity(Intent(this, MyTripsActivity::class.java))
        }

    }
}
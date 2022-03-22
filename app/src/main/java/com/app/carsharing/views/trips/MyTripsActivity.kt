package com.app.carsharing.views.trips

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.app.carsharing.adapters.TripAdapter
import com.app.carsharing.data.datasources.SharedStorage
import com.app.carsharing.data.models.JoinTrip
import com.app.carsharing.data.models.Trip
import com.app.carsharing.databinding.ActivityMyTripsBinding
import com.app.carsharing.viewmodel.NetworkViewModel
import com.app.carsharing.viewmodel.TripViewModel

class MyTripsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyTripsBinding

    private lateinit var networkViewModel: NetworkViewModel

    private lateinit var tripViewModel: TripViewModel
    private lateinit var tripAdapter: TripAdapter

    val trips = ArrayList<Trip>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        networkViewModel = ViewModelProvider(this)[NetworkViewModel::class.java]
        tripViewModel = ViewModelProvider(this)[TripViewModel::class.java]

        binding = ActivityMyTripsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        tripAdapter = TripAdapter(this)
        binding.myTrips.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(context, 2)
            adapter = tripAdapter
        }

        binding.goBack.setOnClickListener {
            finish()
        }
        getData()
    }


    private fun getData() {

        val haveCar = SharedStorage.getHaveCarData(this)
        val uId: String? = SharedStorage.getLoginPhoneData(this)
        networkViewModel.networkState(this).observe(this) {
            if (it) {
                if (haveCar) {
                    tripViewModel.getTripsUserData(uId, haveCar).observe(this) { data ->
                        trips.clear()

                        if (data != null&& data.hasChildren()) {
                            data.children.forEach {
                                val trip = it.getValue(Trip::class.java)!!

                                trip.userId = uId
                                trip.joined = it.child("joins").children.count()
                                trips.add(trip)
                            }
                        }

                        if (trips.isNotEmpty()) {
                            tripAdapter.addData(trips)
                            binding.empty.visibility = View.GONE
                        } else {
                            binding.empty.visibility = View.VISIBLE
                        }
                    }
                } else {
                    tripViewModel.getTripsUserData(null, false).observe(this) { data ->
                        trips.clear()
                        trips.clear()
                        var userId = ""
                        if (data!!.hasChildren()) {
                            data.children.forEach { data2 ->
                                if (data2.hasChildren()) {
                                    userId = data2.key.toString()
                                    data2.children.forEach {
                                        val trip = it.getValue(Trip::class.java)!!
                                        trip.userId = userId

                                        if (it.hasChild("joins")) {
                                            trip.joined = it.child("joins").children.count()

                                            it.child("joins").children.forEach { data3 ->
                                                val join = data3.getValue(JoinTrip::class.java)
                                               // Toast.makeText(this, join.toString()+" "+data3.key+" "+uId+" "+userId, Toast.LENGTH_SHORT).show()
                                                if (join != null) {
                                                    if (join.uid == uId) {
                                                        trips.add(trip)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (trips.isNotEmpty()) {
                            tripAdapter.addData(trips)
                            binding.empty.visibility = View.GONE
                        } else {
                            binding.empty.visibility = View.VISIBLE
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Please open Network", Toast.LENGTH_SHORT).show()
            }
        }

    }

}
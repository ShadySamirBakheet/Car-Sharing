package com.app.carsharing.views.trips

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.carsharing.R
import com.app.carsharing.adapters.ChairAdapter
import com.app.carsharing.adapters.PointArrayAdapter
import com.app.carsharing.adapters.PointTripAdapter
import com.app.carsharing.data.datasources.SharedStorage
import com.app.carsharing.data.models.JoinTrip
import com.app.carsharing.data.models.Trip
import com.app.carsharing.data.models.TripPoint
import com.app.carsharing.data.models.User
import com.app.carsharing.databinding.ActivityTripDetailsBinding
import com.app.carsharing.viewmodel.NetworkViewModel
import com.app.carsharing.viewmodel.TripViewModel
import com.app.carsharing.viewmodel.UserViewModel
import com.app.carsharing.views.chats.ChatActivity
import com.bumptech.glide.Glide

class TripDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTripDetailsBinding

    private lateinit var networkViewModel: NetworkViewModel
    private lateinit var tripViewModel: TripViewModel
    private lateinit var userViewModel: UserViewModel

    private lateinit var pointTripAdapter: PointTripAdapter

    var userId = ""
    var myId = ""
    var tripId = ""
    var pointSelect1 = 0
    var pointSelect2 = 0

    var points = ArrayList<TripPoint>()

    var trip: Trip = Trip()
    var user = User()

    var tripJoin: JoinTrip? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTripDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.isOffline.visibility = GONE
        networkViewModel = ViewModelProvider(this)[NetworkViewModel::class.java]
        tripViewModel = ViewModelProvider(this)[TripViewModel::class.java]
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        tripId = intent.getStringExtra("tripId").toString()

        userId = intent.getStringExtra("userId").toString()

        myId = SharedStorage.getLoginPhoneData(this).toString()


        ///Toast.makeText(this, userId+tripId, Toast.LENGTH_SHORT).show()

        binding.chooseStart.adapter =
            ArrayAdapter(this, R.layout.item_spinner, arrayOf("Cairo", "Cairo", "Cairo", "Cairo"))

        binding.chooseStart2.adapter =
            ArrayAdapter(this, R.layout.item_spinner, arrayOf("Cairo", "Cairo", "Cairo", "Cairo"))

        pointTripAdapter = PointTripAdapter(this)

        binding.points.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = pointTripAdapter
        }

        binding.edit.setOnClickListener {
            startActivity(
                Intent(this, TripEditActivity::class.java)
                    .putExtra("tripId", tripId)
            )
        }

        binding.joinTrip.setOnClickListener {
            startActivity(
                Intent(this, ConfirmJoinActivity::class.java)
                    .putExtra("userId", userId)
                    .putExtra("tripId", tripId)
                    .putExtra("pointSelect", points[pointSelect1].id)
                    .putExtra("pointSelect2", points[pointSelect1].id)
            )
        }

        binding.goChat.setOnClickListener {
            startActivity(
                Intent(this, ChatActivity::class.java).putExtra("userId", userId)
                    .putExtra("userImg", user.userImg)
                    .putExtra("userName", user.userName)
            )
        }

        binding.goBack.setOnClickListener {
            finish()
        }

        getData()

        binding.leaveTrip.setOnClickListener {
            leaveTrip()
        }

        binding.chooseStart.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (parent != null) {
                    pointSelect1 = position
                    if (points.isNotEmpty()) {
                        binding.realPrice.text = getPrice()
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                ////TODO("Not yet implemented")
            }
        }

        binding.chooseStart2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (parent != null) {
                    pointSelect2 = position
                    if (points.isNotEmpty()) {
                        binding.realPrice.text = getPrice()
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                ////TODO("Not yet implemented")
            }
        }


    }

    private fun leaveTrip() {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Are you sure?")
            .setMessage("You will be removed from this trip")
            .setPositiveButton("Ok") { dialogInterface, _ ->
                leaveTripApi()
                dialogInterface.dismiss()
            }
            .setNegativeButton("Cancel") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
        builder.create().show()
    }

    private fun leaveTripApi() {

        networkViewModel.networkState(this).observe(this) {
            if (it) {
                tripViewModel.leaveTrip(tripJoin, tripId, userId)
                finish()
            }

        }
    }

    @SuppressLint("SetTextI18n")
    private fun getData() {

        if (userId == SharedStorage.getLoginPhoneData(this)) {
            binding.joinInfo.visibility = GONE
            binding.joinTrip.visibility = GONE
            binding.goChat.visibility = GONE
            binding.leaveTrip.visibility = GONE
            binding.edit.visibility = VISIBLE
        } else {
            binding.joinTrip.visibility = VISIBLE
            binding.goChat.visibility = VISIBLE
            binding.edit.visibility = GONE
            binding.leaveTrip.visibility = GONE
            binding.joinInfo.visibility = VISIBLE
        }

        networkViewModel.networkState(this).observe(this) {
            if (it) {
                userViewModel.getUserData(userId).observe(this) { dataSnapshot ->
                    if (dataSnapshot != null) {
                        try {
                            user = dataSnapshot.getValue(User::class.java)!!
                        } catch (e: Exception) {
                            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                            Log.e("this", e.message!!)
                        }
                    }
                }

                tripViewModel.getTripData(tripId, userId).observe(this) { data2 ->
                    if (data2 != null) {
                        if (data2.hasChildren()) {

                            trip = data2.getValue(Trip::class.java)!!
                            trip.userId = userId
                            ///  Toast.makeText(this, trip.toString(), Toast.LENGTH_SHORT).show()
                            points.clear()
                            data2.child("points").children.forEach { item ->
                                points.add(item!!.getValue(TripPoint::class.java)!!)
                            }

                            trip.joined = data2.child("joins").children.count()

                            if (data2.child("joins").hasChildren()) {
                                data2.child("joins").children.forEach {
                                    tripJoin = it.getValue(JoinTrip::class.java)
                                    if (tripJoin != null) {
                                        if (tripJoin!!.uid == myId) {

                                            binding.leaveTrip.visibility = VISIBLE
                                            binding.joinTrip.visibility = GONE
                                        }
                                    }
                                }
                            }

                            binding.chairs.apply {
                                setHasFixedSize(true)
                                layoutManager = LinearLayoutManager(
                                    context,
                                    LinearLayoutManager.HORIZONTAL,
                                    false
                                )
                                adapter = ChairAdapter(
                                    this@TripDetailsActivity,
                                    trip.numberSeats ?: 0,
                                    trip.joined ?: 0
                                )
                            }

                            // Toast.makeText(this, "Sead ${trip.numberSeats } joined ${trip.joined}", Toast.LENGTH_SHORT).show()

                            if (trip.numberSeats == trip.joined) {
                                binding.joinTrip.visibility = GONE
                            }

                            binding.type.text = trip.carModel
                            binding.name.text = trip.tripTitle
                            binding.title.text = trip.tripTitle
                            binding.desc.text = trip.tripDesc
                            binding.price.text = trip.tripPrice.toString()
                            binding.realPrice.text = getPrice()

                            binding.time.text =
                                "Start at ${trip.goTime}, and Back at ${trip.backTime}"

                            Glide.with(this).load(trip.tripImage)
                                .placeholder(R.drawable.image1)
                                .into(binding.image)

                            pointTripAdapter.addData(points)
                            binding.chooseStart.adapter = PointArrayAdapter(points, this)
                            binding.chooseStart2.adapter = PointArrayAdapter(points, this)
                        }
                    }
                }
                binding.isOffline.visibility = GONE
                binding.con.visibility = VISIBLE
            } else {
                binding.con.visibility = GONE
                binding.isOffline.visibility = VISIBLE
                Toast.makeText(this, "Open Network", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun getPrice(): String {
        var pointsPrice = points[pointSelect1].pointPrice!!

        if (pointSelect2 > pointSelect1) {
            for (index in pointSelect1..pointSelect2) {
                pointsPrice += points[index].pointPrice!!
            }
            pointsPrice -= points[pointSelect1].pointPrice!!
        }
        return pointsPrice.toString()
    }
}

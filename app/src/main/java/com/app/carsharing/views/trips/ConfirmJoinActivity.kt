package com.app.carsharing.views.trips

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.app.carsharing.adapters.PointArrayAdapter
import com.app.carsharing.data.datasources.SharedStorage
import com.app.carsharing.data.models.JoinTrip
import com.app.carsharing.data.models.TripPoint
import com.app.carsharing.databinding.ActivityConfirmJoinBinding
import com.app.carsharing.viewmodel.NetworkViewModel
import com.app.carsharing.viewmodel.TripViewModel

class ConfirmJoinActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConfirmJoinBinding

    private lateinit var networkViewModel: NetworkViewModel
    private lateinit var tripViewModel: TripViewModel

    var userId = ""
    var tripId = ""
    var pointSelect = ""
    var pointSelect2 = ""
    var select = 1

    var price = 0

    var pointSel: TripPoint? = null

    var points = ArrayList<TripPoint>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        networkViewModel = ViewModelProvider(this)[NetworkViewModel::class.java]
        tripViewModel = ViewModelProvider(this)[TripViewModel::class.java]

        binding = ActivityConfirmJoinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tripId = intent.getStringExtra("tripId").toString()
        userId = intent.getStringExtra("userId").toString()
        pointSelect = intent.getStringExtra("pointSelect").toString()
        pointSelect2 = intent.getStringExtra("pointSelect2").toString()



        binding.goBack.setOnClickListener {
            finish()
        }

        binding.joinTrip.setOnClickListener {
            saveData()
        }

        binding.card.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (isChecked) {
                binding.visaInfo.visibility = View.VISIBLE
            } else {
                binding.visaInfo.visibility = View.GONE
            }
        }

        binding.cardNumber.doOnTextChanged { text, start, before, count ->
            if (text?.length == 16) {
                binding.isDone.visibility = View.VISIBLE
                binding.isNotDone.visibility = View.GONE
            } else {
                binding.isDone.visibility = View.GONE
                binding.isNotDone.visibility = View.VISIBLE
            }
        }

        binding.chooseStart.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (parent != null) {
                    select = position
                    if (points.isNotEmpty()) {
                        price = points[select].pointPrice!!
                        binding.realPrice.text = price.toString()
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                ////TODO("Not yet implemented")
            }

        }

        //getData()

    }

    private fun saveData() {
        val cardNumber = binding.cardNumber.text.toString().trim()
        val month = binding.month.text.toString().trim()
        val year = binding.year.text.toString().trim()
        val cvv = binding.cvv.text.toString().trim()
        val name = binding.userName.text.toString().trim()
        if (binding.cash.isChecked || binding.card.isChecked) {
            val joinTrip = JoinTrip(
                System.currentTimeMillis().toString(),
                name,
                cardNumber,
                pointSelect,
                pointSelect2,
                price,
                SharedStorage.getLoginPhoneData(this).toString(),
                isCash = binding.cash.isChecked && !binding.card.isChecked,
            )

            networkViewModel.networkState(this).observe(this) {
                if (it) {
                    tripViewModel.setJoinTripData(tripId, joinTrip, userId).addOnCompleteListener {
                        Toast.makeText(this, "Join Done", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } else {
                    Toast.makeText(this, "Please Open Network", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "Please Fill All info", Toast.LENGTH_SHORT).show()
        }
    }


    private fun getData() {

        networkViewModel.networkState(this).observe(this) {
            if (it) {
                tripViewModel.getTripPointData(tripId, userId).observe(this) { data2 ->
                    if (data2 != null) {
                        if (data2.hasChildren()) {
                            points.clear()
                            data2.children.forEach { item ->
                                val point = item!!.getValue(TripPoint::class.java)!!
                                points.add(point)
                                if (point.id == pointSelect) {
                                    pointSel = point
                                }
                            }
                            binding.chooseStart.adapter = PointArrayAdapter(points, this)
                            binding.chooseStart.setSelection(points.indexOf(pointSel))

                        }
                    }
                }
            } else {

            }

        }
    }
}

package shady.samir.carsharing.views.trips

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.default
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import shady.samir.carsharing.R
import shady.samir.carsharing.adapters.PointTripAddedAdapter
import shady.samir.carsharing.data.datasources.SharedStorage
import shady.samir.carsharing.data.models.Trip
import shady.samir.carsharing.data.models.TripPoint
import shady.samir.carsharing.databinding.ActivityTripEditBinding
import shady.samir.carsharing.utils.FileUtils
import shady.samir.carsharing.viewmodel.NetworkViewModel
import shady.samir.carsharing.viewmodel.TripViewModel
import shady.samir.carsharing.views.home.HomeActivity
import java.io.File

class TripEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTripEditBinding
    private var adapterPoint: PointTripAddedAdapter? = null

    private lateinit var networkViewModel: NetworkViewModel
    private lateinit var tripViewModel: TripViewModel

    var userId = ""
    var tripId = ""

    val hours1 = arrayOf("Go" ,"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12")
    val hours2 = arrayOf("Back" ,"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12")

    var seatsNum = 0
    var selGo = "01"
    var selBack = "01"

    var points = ArrayList<TripPoint>()

    private var sendingFile: File? = null

    private lateinit var trip: Trip

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        networkViewModel = ViewModelProvider(this)[NetworkViewModel::class.java]
        tripViewModel = ViewModelProvider(this)[TripViewModel::class.java]

        binding = ActivityTripEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.seatNumber.adapter = ArrayAdapter(
            this,
            R.layout.item_spinner,
            arrayOf("Car Seats", "1", "2", "3", "4", "5", "6")
        )

        binding.hourGo.adapter = ArrayAdapter(
           this,
            R.layout.item_spinner,hours1
        )

        binding.hourBack.adapter = ArrayAdapter(
           this,
            R.layout.item_spinner,hours2
        )

        binding.goTime.setOnClickListener {
            if (binding.goTime.text == "AM"){
                binding.goTime.text = "PM"
            }else{
                binding.goTime.text = "AM"
            }
        }

        binding.backTime.setOnClickListener {
            if (binding.backTime.text == "AM"){
                binding.backTime.text = "PM"
            }else{
                binding.backTime.text = "AM"
            }
        }

        tripId = intent.getStringExtra("tripId").toString()
        userId = SharedStorage.getLoginPhoneData(this).toString()

        binding.getImage.setOnClickListener {
            loadImage()
        }

        binding.deleteImg.setOnClickListener {
            binding.setImage.setImageResource(R.drawable.empty)
        }

        binding.progress.visibility = View.GONE

        adapterPoint = PointTripAddedAdapter(this)

        adapterPoint!!.setOnItemClickListener {
            showDialogDeletePoint(it)
        }

        binding.tripAdded.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@TripEditActivity)
            adapter = adapterPoint
        }

        binding.addPoint.setOnClickListener {
            val name = binding.pointName.text.toString().trim()
            val price = binding.pointPrice.text.toString().trim()
            try {
                val point =
                    TripPoint("PointN${adapterPoint?.data?.size!! + 1}", name, price.toInt())
                adapterPoint!!.addPoint(point)
                binding.pointName.setText("")
                binding.pointPrice.setText("")
            } catch (e: Exception) {
                Toast.makeText(this, "Error ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        binding.goBack.setOnClickListener {
            finish()
        }

        binding.edit.setOnClickListener {
            saveTrip()
        }

        binding.delete.setOnClickListener {
            showDialogDeleteTrip()
        }

        getData()
    }

    private fun showDialogDeletePoint(it:TripPoint) {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Delete Point")
            .setMessage("Are you want to Delete Point")
            .setPositiveButton("Ok") { dialogInterface, _ ->
                adapterPoint!!.remove(it)
                dialogInterface.dismiss()
            }
            .setNegativeButton("Cancel") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
        builder.create().show()
    }

    private fun showDialogDeleteTrip() {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Delete Trip")
            .setMessage("Are you want to Delete Trip")
            .setPositiveButton("Ok") { dialogInterface, _ ->
                tripViewModel.deleteTripData(tripId,userId)
                finish()
                dialogInterface.dismiss()
            }
            .setNegativeButton("Cancel") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
        builder.create().show()
    }

    private fun getData() {

        networkViewModel.networkState(this).observe(this) {
            if (it) {
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

                            binding.carModel.setText(trip.carModel)
                            binding.title.setText(trip.tripTitle)
                            binding.desc.setText(trip.tripDesc)
                            binding.price.setText(trip.tripPrice.toString())
                            //  binding.realPrice.text = points[pointSelect].pointPrice.toString()+" EGP"

                            setHour(trip.goTime,trip.backTime)

                             seatsNum = trip.numberSeats!!

                            binding.seatNumber.setSelection(trip.numberSeats!!)


                            Glide.with(this).load(trip.tripImage)
                                .placeholder(R.drawable.image1)
                                .into(binding.setImage)

                            adapterPoint?.addData(points)
                            // binding.chooseStart.adapter = PointArrayAdapter(points,this)
                        }
                    }
                }
            } else {

            }

        }
    }

    private fun setHour(goTime: String?, backTime: String?) {
        if (goTime != null && goTime.isNotEmpty()){
            val go = goTime.split(":")
            binding.goTime.text = go[1]
            binding.hourGo.setSelection(hours1.indexOf(go[0]))
        }
        if (backTime != null && backTime.isNotEmpty()){
            val back = backTime.split(":")
            binding.backTime.text = back[1]
            binding.hourBack.setSelection(hours2.indexOf(back[0]))
        }
    }

    private fun saveTrip() {

        binding.progress.visibility = View.VISIBLE

        val title = binding.title.text.toString().trim()
        val price = binding.price.text.toString().trim()
        val carModel = binding.carModel.text.toString().trim()
        val description = binding.desc.text.toString().trim()

        seatsNum = binding.seatNumber.selectedItemPosition

        try {
            if (title.isNotEmpty() && price.toInt() > 0 && carModel.isNotEmpty() && description.isNotEmpty() && seatsNum > 0) {
                trip.tripTitle = title
                trip.tripPrice = price.toInt()
                trip.carModel = carModel
                trip.tripDesc = description
                trip.numberSeats = seatsNum
                // points = adapterPoint!!.data

                networkViewModel.networkState(this).observe(this) {
                    if (it) {
                        if (sendingFile != null) {
                            val ref = FirebaseStorage.getInstance().reference.child("trips_images")
                                .child(trip.id + ".png")

                            var file = sendingFile
                            GlobalScope.launch {
                                file = Compressor.compress(this@TripEditActivity, sendingFile!!) {
                                    default(format = Bitmap.CompressFormat.PNG)
                                }
                            }

                            ref.putFile(file!!.toUri()).addOnCompleteListener {
                                it.result!!.storage.downloadUrl.addOnSuccessListener { uri ->
                                    if (uri != null) {
                                        trip.tripImage = uri.toString()
                                    }
                                    saveDate()
                                }
                            }.addOnFailureListener {
                                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                            }

                        } else {
                            saveDate()
                        }
                    } else {
                        Toast.makeText(this, "Open Network", Toast.LENGTH_SHORT).show()
                        binding.progress.visibility = View.GONE
                    }
                }

            } else {
                binding.progress.visibility = View.GONE
                Toast.makeText(this, "fill all argument", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            binding.progress.visibility = View.GONE
            Toast.makeText(this, "fill argument ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveDate() {
        val uid = SharedStorage.getLoginPhoneData(this).toString()
        var index = 0


        val go = hours1[binding.hourGo.selectedItemPosition]+":"+binding.goTime.text.toString()
        val back =  hours2[binding.hourBack.selectedItemPosition]+":"+binding.backTime.text.toString()

        trip.backTime = back
        trip.goTime = go

        tripViewModel.setTripData(trip, uid)

        adapterPoint!!.data.forEach { point ->
            index++
            tripViewModel.setTripPointData(
                trip.id.toString(),
                TripPoint(point.id, point.pointName, point.pointPrice),
                uid
            )
        }
        binding.progress.visibility = View.GONE

        startActivity(
            Intent(this, HomeActivity::class.java)
        )
    }

    private fun loadImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.apply {
            type = "image/*"
        }
        startActivityForResult(intent, 1)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null) {
            when (requestCode) {
                1 -> {
                    loadImageFun(data)
                }

            }
        } else {
            Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()
        }
    }

    private fun loadImageFun(data: Intent) {
        if (data.data != null) {
            val uri = data.data!!


            sendingFile = File(FileUtils.getSmartFilePath(this, data.data!!) ?: "")

            binding.setImage.setImageURI(uri)

            Toast.makeText(this, "Send File", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Error File", Toast.LENGTH_LONG).show()
        }
    }
}
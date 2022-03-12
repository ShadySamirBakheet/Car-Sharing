package shady.samir.carsharing.views.home.ui

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.storage.FirebaseStorage
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.default
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import log.apps.makkahsandwich.Utils.PermissionCheck
import shady.samir.carsharing.R
import shady.samir.carsharing.adapters.PointTripAddedAdapter
import shady.samir.carsharing.data.datasources.SharedStorage
import shady.samir.carsharing.data.models.Trip
import shady.samir.carsharing.data.models.TripPoint
import shady.samir.carsharing.databinding.FragmentAddTripBinding
import shady.samir.carsharing.utils.FileUtils
import shady.samir.carsharing.viewmodel.NetworkViewModel
import shady.samir.carsharing.viewmodel.TripViewModel
import shady.samir.carsharing.views.trips.TripDetailsActivity
import java.io.File

class AddTripFragment : Fragment() {

    private var _binding: FragmentAddTripBinding? = null
    private var adapterPoint: PointTripAddedAdapter? = null

    private lateinit var networkViewModel: NetworkViewModel
    private lateinit var tripViewModel: TripViewModel


    private lateinit var trip: Trip

    var sendingFile: File? = null

    private val binding get() = _binding!!

    val hours1 =
        arrayOf("Go", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12")
    val hours2 =
        arrayOf("Back", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12")

    var seatsNum = 0
    var selGo = "01"
    var selBack = "01"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTripBinding.inflate(inflater, container, false)
        val root: View = binding.root

        if (SharedStorage.getHaveCarData(
                requireContext()
            )
        ) {
            binding.addLay.visibility = VISIBLE
            binding.noCar.visibility = GONE
        } else {
            binding.addLay.visibility = GONE
            binding.noCar.visibility = VISIBLE
        }

        binding.progress.visibility = GONE
        networkViewModel = ViewModelProvider(this)[NetworkViewModel::class.java]
        tripViewModel = ViewModelProvider(this)[TripViewModel::class.java]

        binding.seatNumber.adapter = ArrayAdapter(
            requireContext(),
            R.layout.item_spinner,
            arrayOf("Car Seats", "1", "2", "3", "4", "5", "6")
        )

        binding.hourGo.adapter = ArrayAdapter(
            requireContext(),
            R.layout.item_spinner, hours1
        )

        binding.hourBack.adapter = ArrayAdapter(
            requireContext(),
            R.layout.item_spinner,
            hours2
        )

        binding.goTime.setOnClickListener {
            if (binding.goTime.text == "AM") {
                binding.goTime.text = "PM"
            } else {
                binding.goTime.text = "AM"
            }
        }

        binding.backTime.setOnClickListener {
            if (binding.backTime.text == "AM") {
                binding.backTime.text = "PM"
            } else {
                binding.backTime.text = "AM"
            }
        }

        adapterPoint = PointTripAddedAdapter(context)

        adapterPoint!!.setOnItemClickListener {
            adapterPoint!!.remove(it)
        }

        binding.tripAdded.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = adapterPoint
        }

        binding.addPoint.setOnClickListener {
            addToData(true)
        }

        binding.submit.setOnClickListener {
            saveTrip()
        }

        binding.getImage.setOnClickListener {

            PermissionCheck.readAndWriteExternalStorage(context)
            loadImage()
        }


        return root
    }

    private fun addToData(b: Boolean) {
        val name = binding.pointName.text.toString().trim()
        val price = binding.pointPrice.text.toString().trim()
        try {
            val point =
                TripPoint("PointN${adapterPoint?.data?.size!! + 1}", name, price.toInt())
            adapterPoint!!.addPoint(point)
            binding.pointName.setText("")
            binding.pointPrice.setText("")
        } catch (e: Exception) {
            if (b){
                Toast.makeText(context, "Error data to saving", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveTrip() {
        binding.progress.visibility = VISIBLE
        val title = binding.title.text.toString().trim()
        val price = binding.price.text.toString().trim()
        val carModel = binding.carModel.text.toString().trim()
        val description = binding.description.text.toString().trim()

        addToData(false)

        seatsNum = binding.seatNumber.selectedItemPosition


        val go = hours1[binding.hourGo.selectedItemPosition] + ":" + binding.goTime.text.toString()
        val back =
            hours2[binding.hourBack.selectedItemPosition] + ":" + binding.backTime.text.toString()

        try {
            if (title.isNotEmpty() && price.toInt() > 0 && carModel.isNotEmpty() && description.isNotEmpty() && seatsNum > 0) {
                trip = Trip(
                    tripTitle = title,
                    tripPrice = price.toInt(),
                    carModel = carModel,
                    tripDesc = description,
                    numberSeats = seatsNum,
                    id = "tripN" + System.currentTimeMillis(),
                    goTime = go, backTime = back,
                    // points = adapterPoint!!.data
                )
                networkViewModel.networkState(context).observe(viewLifecycleOwner) {
                    if (it) {
                        if (sendingFile != null) {
                            val ref = FirebaseStorage.getInstance().reference.child("trips_images")
                                .child(trip.id + ".png")

                            var file = sendingFile
                            GlobalScope.launch {
                                file = Compressor.compress(requireContext(), sendingFile!!) {
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
                                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                            }

                        } else {
                            saveDate()
                        }
                    } else {
                        Toast.makeText(context, "Open Network", Toast.LENGTH_SHORT).show()
                        binding.progress.visibility = GONE
                    }
                }
            } else {
                binding.progress.visibility = GONE
                Toast.makeText(context, "fill all argument", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            binding.progress.visibility = GONE
            Toast.makeText(context, "fill all argument", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveDate() {
        val uid = SharedStorage.getLoginPhoneData(requireContext()).toString()
        var index = 0
        tripViewModel.setTripData(trip, uid).addOnSuccessListener {
            binding.title.setText("")
            binding.price.setText("")
            binding.carModel.setText("")

            binding.setImage.setImageResource(R.drawable.empty)
            binding.description.setText("")

            binding.seatNumber.adapter = ArrayAdapter(
                requireContext(),
                R.layout.item_spinner,
                arrayOf("Car Seats", "1", "2", "3", "4", "5", "6")
            )

            binding.hourGo.adapter = ArrayAdapter(
                requireContext(),
                R.layout.item_spinner, hours1
            )

            binding.hourBack.adapter = ArrayAdapter(
                requireContext(),
                R.layout.item_spinner,
                hours2
            )
            adapterPoint!!.data.forEach { point ->
                index++
                tripViewModel.setTripPointData(
                    trip.id.toString(),
                    TripPoint(point.id, point.pointName, point.pointPrice),
                    uid
                )
            }

            adapterPoint!!.dataClear()
            binding.progress.visibility = GONE

            startActivity(
                Intent(context, TripDetailsActivity::class.java)
                    .putExtra("userId", uid)
                    .putExtra("tripId", trip.id)
            )
        }

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
            Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
        }
    }


    private fun loadImageFun(data: Intent) {
        if (data.data != null) {
            val uri = data.data!!

            sendingFile = File(FileUtils.getSmartFilePath(requireContext(), data.data!!) ?: "")

            binding.setImage.setImageURI(uri)

            Toast.makeText(context, "Send File", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "Error File", Toast.LENGTH_LONG).show()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.app.carsharing.views.home.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.app.carsharing.adapters.TripAdapter
import com.app.carsharing.data.datasources.SharedStorage
import com.app.carsharing.data.models.Trip
import com.app.carsharing.databinding.FragmentHomeBinding
import com.app.carsharing.viewmodel.NetworkViewModel
import com.app.carsharing.viewmodel.TripViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private lateinit var networkViewModel: NetworkViewModel

    private lateinit var tripViewModel: TripViewModel
    private lateinit var tripAdapter: TripAdapter

    private val binding get() = _binding!!

    val trips = ArrayList<Trip>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        networkViewModel = ViewModelProvider(this)[NetworkViewModel::class.java]
        tripViewModel = ViewModelProvider(this)[TripViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        tripAdapter = TripAdapter(context)
        binding.trips.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(context, 2)
            adapter = tripAdapter
        }

        binding.inputSearch.doOnTextChanged { text, start, before, count ->
            val new = ArrayList<Trip>()
            for ( trip in trips){
                if (trip.tripTitle!!.lowercase().contains(text.toString().trim().lowercase())){
                    new.add(trip)
                }
            }
            tripAdapter.addData(new)
            if (new.size==0){
                binding.empty.visibility = VISIBLE
            }else{
                binding.empty.visibility = GONE
            }
        }

        getData()
        return root
    }

    private fun getData() {

        networkViewModel.networkState(context).observe(viewLifecycleOwner) { it ->
            if (it) {
                binding.isOffline.visibility = GONE
                binding.home.visibility = VISIBLE
               val haveCar = SharedStorage.getHaveCarData(requireContext())
                val uId:String? = SharedStorage.getLoginPhoneData(requireContext())
          if (haveCar){
              tripViewModel.getTripsData(haveCar,uId).observe(viewLifecycleOwner) { data ->
                  trips.clear()
                  if (data!=null && data.hasChildren()) {
                      data.children.forEach {
                          val trip = it.getValue(Trip::class.java)!!
                          trip.userId = uId
                          trip.joined = it.child("joins").children.count()
                          if (trip.numberSeats!! > trip.joined!!) {
                              trips.add(trip)
                          }
                      }
                  }

                  if (trips.isNotEmpty()) {
                      tripAdapter.addData(trips)
                      binding.empty.visibility = GONE
                  }else{
                      binding.empty.visibility = VISIBLE
                  }
              }
          }else{
              tripViewModel.getTripsData(false,null).observe(viewLifecycleOwner) { data ->
                  trips.clear()
                  var userId = ""
                  if (data!!.hasChildren()) {
                      data.children.forEach { data2 ->
                          if (data2.hasChildren()) {
                              userId = data2.key.toString()
                              data2.children.forEach {
                                  val trip = it.getValue(Trip::class.java)!!
                                  trip.userId = userId
                                  trip.joined = it.child("joins").children.count()
                                  if (trip.numberSeats!! > trip.joined!!) {
                                      trips.add(trip)
                                  }
                              }
                          }
                      }
                  }

                  if (trips.isNotEmpty()) {
                      tripAdapter.addData(trips)
                      binding.empty.visibility = GONE
                  }else{
                      binding.empty.visibility = VISIBLE
                  }
              }
          }
            } else {

                binding.isOffline.visibility = VISIBLE
                binding.home.visibility = GONE
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.app.carsharing.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.app.carsharing.data.datasources.FirebaseQueryLiveData
import com.app.carsharing.data.models.JoinTrip
import com.app.carsharing.data.models.Trip
import com.app.carsharing.data.models.TripPoint

class TripViewModel (application: Application): AndroidViewModel(application) {

    private val tripRef = FirebaseDatabase.getInstance().getReference("trips/")

    private var liveData = FirebaseQueryLiveData(tripRef)

    fun getTripsData(haveCar: Boolean,uid: String?): LiveData<DataSnapshot?> {
        if (haveCar){
           return FirebaseQueryLiveData(tripRef.child(uid.toString()));
        }
        return liveData
    }

    fun getTripsUserData(uid: String?, haveCar: Boolean): LiveData<DataSnapshot?> {
        if (haveCar){
            return FirebaseQueryLiveData(tripRef.child(uid.toString()));
        }
        return liveData
    }

    fun getTripData(id: String,uid:String): LiveData<DataSnapshot?> {
        return FirebaseQueryLiveData(tripRef.child("$uid/$id"))
    }

    fun getTripPointData(id: String,uid:String): LiveData<DataSnapshot?> {
        return FirebaseQueryLiveData(tripRef.child("$uid/$id/points"))
    }


    fun deleteTripData(id: String,uid:String): Task<Void> {
        return tripRef.child("$uid/$id").removeValue()
    }

    fun setTripData(trip: Trip,uid:String): Task<Void> {
        return tripRef.child("$uid/${trip.id}").setValue(trip)
    }

    fun setTripPointData(id:String,trip: TripPoint,uid:String): Task<Void> {
        return tripRef.child("$uid/$id/points/${trip.id}").setValue(trip)
    }

    fun setJoinTripData(id:String, trip: JoinTrip, uid:String): Task<Void> {
        return tripRef.child("$uid/$id/joins/${trip.id}").setValue(trip)
    }

}
package com.app.carsharing.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.app.carsharing.data.datasources.FirebaseQueryLiveData
import com.app.carsharing.data.models.Message
import com.app.carsharing.utils.Methods

class MessageViewModel(application: Application): AndroidViewModel(application)   {

    private val msgRef = FirebaseDatabase.getInstance().getReference("messages/")

    private val liveData = FirebaseQueryLiveData(msgRef)

    fun getUsersData(): LiveData<DataSnapshot?> {
        return liveData
    }

    fun getMessagesData(rootUsers:String): LiveData<DataSnapshot?> {
        return FirebaseQueryLiveData(msgRef.child(rootUsers))
    }

    fun getMessagesLastData(rootUsers:String): LiveData<DataSnapshot?> {
        return FirebaseQueryLiveData(msgRef.child(rootUsers).limitToLast(1))
    }

    fun setMessageData(message: Message): Task<Void> {
        return msgRef.child(Methods.getMessageId(message.senderId.toString(),
            message.receiverID.toString()
        )+"/"+message.msgID).setValue(message)
    }

}
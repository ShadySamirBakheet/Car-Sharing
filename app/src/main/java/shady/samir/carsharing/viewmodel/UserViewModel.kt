package shady.samir.carsharing.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import shady.samir.carsharing.data.datasources.FirebaseQueryLiveData
import shady.samir.carsharing.data.models.Message
import shady.samir.carsharing.data.models.MessageUser
import shady.samir.carsharing.data.models.User
import shady.samir.carsharing.utils.Methods


class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val userRef = FirebaseDatabase.getInstance().getReference("users/")

    private val liveData = FirebaseQueryLiveData(userRef)

    private val msgRef = FirebaseDatabase.getInstance().getReference("messages/")

    fun getUsersData(): LiveData<DataSnapshot?> {
        return liveData
    }


    private fun getMessagesLastData(rootUsers: String): LiveData<DataSnapshot?> {
        return FirebaseQueryLiveData(msgRef.child(rootUsers).limitToLast(1))
    }


    fun getUsersWithListMsg(myPhone: String): LiveData<ArrayList<MessageUser>> {

        val responseLiveDataResult: LiveData<ArrayList<MessageUser>> = liveData {
            val usersMsg: ArrayList<MessageUser> = ArrayList()
            getUsersData().value.apply {
                usersMsg.clear()
                if (this != null) {
                    for (item in this.children) {
                        try {
                            val user = item.getValue(User::class.java)!!
                            val userMsg = MessageUser(user, null, null)
                            getMessagesLastData(
                                Methods.getMessageId(
                                    myPhone,
                                    user.id.toString()
                                )
                            ).value.apply {
                                if (this != null) {
                                    if (this.hasChildren()) {
                                        var msg: Message? = null
                                        this.children.forEach { item ->
                                            msg = item.getValue(Message::class.java)
                                        }
                                        if (msg != null) {
                                            userMsg.date =   Methods.getDateString(msg!!.date)
                                            userMsg.message = msg!!.dataMsg.toString()
                                            usersMsg.add(userMsg)
                                            emit(usersMsg)
                                        }
                                    }
                                }
                            }

                        } catch (e: Exception) {
                            Log.e("Error", e.message.toString())
                        }
                    }
                    Log.e("MSG", usersMsg.toString())

                    emit(usersMsg)
                }
            }
        }
        return responseLiveDataResult
    }

    fun getUserData(phoneNumber: String): LiveData<DataSnapshot?> {
        return FirebaseQueryLiveData(userRef.child(phoneNumber))
    }

    fun setUserData(user: User): Task<Void> {
        return userRef.child(user.userPhone.toString()).setValue(user)
    }

}
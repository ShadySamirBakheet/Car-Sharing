package com.app.carsharing.views.chats

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.carsharing.adapters.MessageAdapter
import com.app.carsharing.data.datasources.SharedStorage
import com.app.carsharing.data.models.Message
import com.app.carsharing.data.models.MessageModel
import com.app.carsharing.databinding.ActivityChatBinding
import com.app.carsharing.utils.Methods
import com.app.carsharing.viewmodel.MessageViewModel
import com.app.carsharing.viewmodel.NetworkViewModel
import java.util.*
import kotlin.collections.ArrayList

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding

    private lateinit var networkViewModel: NetworkViewModel
    private lateinit var messageViewModel: MessageViewModel

    private var adapterMessage: MessageAdapter? = null

   private lateinit var listMsg:ArrayList<MessageModel>

   private var userId="";
   private var userName="";
   private var userImg="";
   private var myId=""
   private var myImage=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        networkViewModel = ViewModelProvider(this)[NetworkViewModel::class.java]
        messageViewModel = ViewModelProvider(this)[MessageViewModel::class.java]

        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.goBack.setOnClickListener {
            finish()
        }

        binding.btnSend.setOnClickListener {
            sendMsg()
        }

        myId = SharedStorage.getLoginPhoneData( this)!!
        myImage = SharedStorage.getLoginImagesData( this)!!
        userId = intent.getStringExtra("userId").toString()
        userName = intent.getStringExtra("userName").toString()
        userImg = intent.getStringExtra("userImg").toString()

     //   Toast.makeText(this, userId, Toast.LENGTH_SHORT).show()
        binding.userName.text = userName

        val manager = LinearLayoutManager(this)
        manager.stackFromEnd = true

        adapterMessage = MessageAdapter(this,myImage,userImg,myId)

        binding.messages.apply {
            setHasFixedSize( true)
            layoutManager = manager
            adapter = adapterMessage
        }

        loadTestChat()

    }

    private fun sendMsg() {
        val msg = binding.msgInput.text.toString().toString()

        if (msg.isNotEmpty()){
            val message = Message(System.currentTimeMillis().toString(),myId,userId,msg, Date())
            messageViewModel.setMessageData(message)
        }

        binding.msgInput.setText("")
    }

    private fun loadTestChat() {
        listMsg = ArrayList()


        networkViewModel.networkState(this).observe(this) {
            binding.progress.visibility = View.GONE
            if (it) {
                messageViewModel.getMessagesData(Methods.getMessageId(userId,myId)).observe(this) {
                    if (it!= null){
                        listMsg.clear()
                        for (item in it.children){
                            val message = item.getValue(Message::class.java)
                            if (message != null){
                                val messageModel = MessageModel(
                                    message.msgID.toString(),
                                    message.senderId.toString(),
                                    message.receiverID.toString(),
                                    message.dataMsg.toString()
                                   ,Methods.getDateString(message.date),Methods.getTimeString(message.date),message.senderId != myId,)

                                listMsg.add(messageModel)
                            }
                        }


                        if (listMsg.isNotEmpty()){
                            adapterMessage?.addData(listMsg)
                            binding.empty.visibility = View.GONE
                        }else{
                            binding.empty.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }

    }

}


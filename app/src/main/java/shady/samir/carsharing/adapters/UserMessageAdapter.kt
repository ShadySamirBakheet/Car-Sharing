package shady.samir.carsharing.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import shady.samir.carsharing.R
import shady.samir.carsharing.data.models.Message
import shady.samir.carsharing.data.models.MessageUser
import shady.samir.carsharing.data.models.User
import shady.samir.carsharing.utils.Methods
import shady.samir.carsharing.viewmodel.MessageViewModel
import shady.samir.carsharing.views.chats.ChatActivity
import java.util.ArrayList


class UserMessageAdapter(
    private val context: Context?,
    val myPhone: String,
    val viewLifecycleOwner: LifecycleOwner,
    val messageViewModel: MessageViewModel
) : RecyclerView.Adapter<UserMessageAdapter.ViewHolder>() {

    private var users: ArrayList<User> = ArrayList()
    private var usersMsg: ArrayList<MessageUser> = ArrayList()
    private var  size=0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_user_message , parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = usersMsg[position]
        holder.apply {
            userName.text = item.user.userName
            lastDate.text = item.date
            lastMsg.text = item.message
/*
            messageViewModel.getMessagesLastData(
                Methods.getMessageId(
                    myPhone,
                    item.user.userPhone.toString()
                )
            ).observe(viewLifecycleOwner) {mags->
                if (mags != null) {
                    if (mags.hasChildren()) {
                        var msg: Message?
                        mags.children.forEach { item ->
                            msg = item.getValue(Message::class.java)
                            Log.d("LOG_TAG", msg.toString())
                            if (msg != null) {
                                lastDate.text = Methods.getDateString(msg!!.date)
                                lastMsg.text = msg!!.dataMsg
                            }
                        }
                    }
                }

            }*/

            if (item.user.userImg != null){
                if (context != null) {
                    Glide.with(context).load(item.user.userImg)
                        .placeholder(R.drawable.user)
                        .into(userimg)
                }
            }

            itemView.setOnClickListener {
                context?.startActivity(Intent(context, ChatActivity::class.java)
                    .putExtra("userId",item.user.userPhone)
                    .putExtra("userImg",item.user.userImg)
                    .putExtra("userName",item.user.userName))
            }
        }

    }


    private var onItemClickListener: ((String) -> Unit)? = null

    fun setOnItemClickListener(listener: (String) -> Unit) {
        onItemClickListener = listener
    }


    override fun getItemCount(): Int {
        return size
    }

    fun addData(users: ArrayList<User>) {
        this.users = users
        size = users.size
        notifyDataSetChanged()
    }

    fun addDataMsg(usersMsg: ArrayList<MessageUser>) {
        for (m1 in usersMsg) {
            if (m1.user.userPhone == myPhone){
                usersMsg.remove(m1)
                break
            }
        }
        this.usersMsg = usersMsg
        size = this.usersMsg.size
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userName: TextView =itemView.findViewById(R.id.userName)
        var lastMsg: TextView =itemView.findViewById(R.id.lastMsg)
        var lastDate: TextView =itemView.findViewById(R.id.lastDate)
        var userimg: ImageView =itemView.findViewById(R.id.userimg)
    }
}
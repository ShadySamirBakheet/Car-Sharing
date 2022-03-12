package shady.samir.carsharing.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import shady.samir.carsharing.R
import shady.samir.carsharing.data.models.MessageModel
import java.lang.Exception

class MessageAdapter (
    private val context: Context?,
    private val myImage: String,
    private val userImage: String,
    private val myId: String
) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    var listMsg:ArrayList<MessageModel>?=null

    var size=0;

    override fun getItemViewType(position: Int): Int {
        var messageModel = listMsg?.get(position)
        if (messageModel != null) {
            return if (messageModel.isReceiver){ 1 }else{ 2 }
        }
        return  -1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return  if (viewType == 1){
            val layoutInflater = LayoutInflater.from(parent.context)
            val view: View = layoutInflater.inflate(R.layout.item_chat_in, parent, false)
            ViewHolder(view)
        }else{
            val layoutInflater = LayoutInflater.from(parent.context)
            val view: View = layoutInflater.inflate(R.layout.item_chat_out, parent, false)
            ViewHolder(view)
        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listMsg!![position]
        holder.apply {
            try {
                if (position<size &&  item.isReceiver == listMsg!![position+1].isReceiver){
                    time.visibility = GONE
                }else{
                    time.visibility = VISIBLE
                }
            }catch (e:Exception){}

            try {
                if (position < size && item.isReceiver == listMsg!![position+1].isReceiver){
                    img.visibility = INVISIBLE
                }else{
                    if (myId == item.senderId){

                        Glide.with(context!!).load(myImage)
                            .placeholder(R.drawable.user)
                            .into(userImg)
                    }else{
                        Glide.with(context!!).load(userImage)
                            .placeholder(R.drawable.user)
                            .into(userImg)
                    }
                    img.visibility = VISIBLE
                }
            }catch (e:Exception){}

            try {
                if (position > 0 && item.date == listMsg!![position-1].date){
                    date.visibility = GONE
                }else{
                    date.text = item.date
                    if (myId == item.senderId){
                        Glide.with(context!!).load(myImage)
                            .placeholder(R.drawable.user)
                            .into(userImg)
                    }else{
                        Glide.with(context!!).load(userImage)
                            .placeholder(R.drawable.user)
                            .into(userImg)
                    }
                    img.visibility = VISIBLE
                    date.visibility = VISIBLE
                }
            }catch (e:Exception){}

            if (position == size-1 ||  position == 0){
                if (myId == item.senderId){
                    Glide.with(context!!).load(myImage)
                        .placeholder(R.drawable.user)
                        .into(userImg)
                }else{
                    Glide.with(context!!).load(userImage)
                        .placeholder(R.drawable.user)
                        .into(userImg)
                }
            }

            msgTxt.text = item.dataMsg
            time.text = item.time
        }
    }


    private var onItemClickListener: ((String) -> Unit)? = null

    fun setOnItemClickListener(listener: (String) -> Unit) {
        onItemClickListener = listener
    }


    override fun getItemCount(): Int {
        return size
    }

    fun addData(listMsg: ArrayList<MessageModel>) {
        this.listMsg  = listMsg
        size = listMsg.size
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var time: TextView =itemView.findViewById(R.id.time)
        var date: TextView =itemView.findViewById(R.id.date)
        var msgTxt: TextView =itemView.findViewById(R.id.msgTxt)
        var userImg: ImageView =itemView.findViewById(R.id.userImg)
        var img: CardView =itemView.findViewById(R.id.img)
    }
}
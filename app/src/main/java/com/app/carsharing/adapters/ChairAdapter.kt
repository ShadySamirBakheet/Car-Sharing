package com.app.carsharing.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.app.carsharing.R

class ChairAdapter (private val context: Context?, val size:Int, private val value:Int) : RecyclerView.Adapter<ChairAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_chair , parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            if (position < value){
                image.setImageResource(R.drawable.ic_chair2)
            }else {
                image.setImageResource(R.drawable.ic_chair)
            }
        }
    }


    override fun getItemCount(): Int {
        return size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image: ImageView =itemView.findViewById(R.id.image)
    }
}
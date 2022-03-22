package com.app.carsharing.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.carsharing.R

class CodeAdapter  (private val context: Context?) : RecyclerView.Adapter<CodeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_code , parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.chooseCode.apply {
            text = "+20"
            setOnClickListener {
                onItemClickListener.let {
                    if (it != null) {
                        it("+20")
                    }
                }
            }
        }
    }


    private var onItemClickListener: ((String) -> Unit)? = null

    fun setOnItemClickListener(listener: (String) -> Unit) {
        onItemClickListener = listener
    }


    override fun getItemCount(): Int {
        return 6
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var chooseCode: TextView =itemView.findViewById(R.id.chooseCode)
    }
}
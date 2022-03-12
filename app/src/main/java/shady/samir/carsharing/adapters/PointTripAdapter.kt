package shady.samir.carsharing.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import shady.samir.carsharing.R
import shady.samir.carsharing.data.models.PointTrip
import shady.samir.carsharing.data.models.TripPoint
import java.util.ArrayList

class PointTripAdapter (private val context: Context?) : RecyclerView.Adapter<PointTripAdapter.ViewHolder>() {
   var points = ArrayList<TripPoint>()
    var size =0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_point_trip, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = points[position]
        holder.apply {
            pointName.text = item.pointName
            pointNum.text = "Point ${position+1}"
        }
    }


    private var onItemClickListener: ((String) -> Unit)? = null

    fun setOnItemClickListener(listener: (String) -> Unit) {
        onItemClickListener = listener
    }


    override fun getItemCount(): Int {
        return size
    }

    fun addData(points: ArrayList<TripPoint>) {
        this.points = points
        size = points.size
        notifyDataSetChanged()

    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var pointName: TextView =itemView.findViewById(R.id.pointName)
        var pointNum: TextView =itemView.findViewById(R.id.pointNum)
    }
}
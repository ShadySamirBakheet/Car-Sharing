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

class PointTripAddedAdapter (private val context: Context?) : RecyclerView.Adapter<PointTripAddedAdapter.ViewHolder>() {

    var data: ArrayList<TripPoint> = ArrayList()
    private var size =0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_point_trip_added, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val  item = data[position]

        holder.apply {
            pointName.text = item.pointName
            pointPrice.text = item.pointPrice.toString()
            itemView.setOnClickListener {
                onItemClickListener.let {
                    if (it != null) {
                        it(item)
                    }
                }
            }
        }
    }


    private var onItemClickListener: ((TripPoint) -> Unit)? = null

    fun setOnItemClickListener(listener: (TripPoint) -> Unit) {
        onItemClickListener = listener
    }


    override fun getItemCount(): Int {
        return size
    }

    fun addPoint(pointTrip: TripPoint) {
        data.add(pointTrip)
        size = data.size
        notifyDataSetChanged()
    }

    fun remove(pointTrip: TripPoint) {
        data.remove(pointTrip)
        size = data.size
        notifyDataSetChanged()
    }

    fun addData(points: ArrayList<TripPoint>) {
        data = points;
        size = data.size
        notifyDataSetChanged()
    }

    fun dataClear() {
        data.clear()
        size=0
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var pointName: TextView =itemView.findViewById(R.id.pointName)
        var pointPrice: TextView =itemView.findViewById(R.id.pointPrice)
    }
}

package shady.samir.carsharing.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import shady.samir.carsharing.R
import shady.samir.carsharing.data.models.Trip
import shady.samir.carsharing.views.trips.TripDetailsActivity

class TripAdapter(private val context: Context?) : RecyclerView.Adapter<TripAdapter.ViewHolder>() {

    private var trips: ArrayList<Trip> = ArrayList()
    private var size = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_trip, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = trips[position]
        holder.apply {
            name.text = item.tripTitle
            price.text = item.tripPrice.toString()
            type.text = item.carModel
            seatNum.text = (item.numberSeats?.minus(item.joined!!)).toString()

            if ((item.numberSeats?.minus(item.joined!!)) == 0){
                remid.visibility = View.GONE
                doneTrip.visibility = View.VISIBLE
            }else{
                remid.visibility = View.VISIBLE
                doneTrip.visibility = View.GONE
            }

            Glide.with(context!!).load(item.tripImage)
                .placeholder(R.drawable.image1)
                .into(image)

            itemView.setOnClickListener {
                context.startActivity(
                    Intent(context, TripDetailsActivity::class.java)
                        .putExtra("userId", item.userId)
                        .putExtra("tripId", item.id)
                )
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

    fun addData(trips: ArrayList<Trip>) {
        this.trips = trips
        size = trips.size
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView = itemView.findViewById(R.id.name)
        var price: TextView = itemView.findViewById(R.id.price)
        var type: TextView = itemView.findViewById(R.id.type)
        var seatNum: TextView = itemView.findViewById(R.id.seatNum)
        var doneTrip: TextView = itemView.findViewById(R.id.doneTrip)
        var image: ImageView = itemView.findViewById(R.id.image)
        var remid: LinearLayout = itemView.findViewById(R.id.remid)
    }
}
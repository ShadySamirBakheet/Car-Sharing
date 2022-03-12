package shady.samir.carsharing.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckedTextView
import shady.samir.carsharing.R
import shady.samir.carsharing.data.models.TripPoint

class PointArrayAdapter  (val dataSource: ArrayList<TripPoint>, var context: Context): BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    init {
        //dataSource.add(0,AreasResponse.Data("",0,context.getString(R.string.area),0,0,""))
    }

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItem(position: Int): TripPoint{
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val vh: ItemHolder
        if (convertView == null) {
            view = inflater.inflate(R.layout.item_spinner, parent, false)
            vh = ItemHolder(view)
            view?.tag = vh
        } else {
            view = convertView
            vh = view.tag as ItemHolder
        }
        val item = dataSource[position]

        vh.itemName.text = item.pointName

        return view
    }


    private class ItemHolder(row: View?) {
        val itemName: CheckedTextView

        init {
            itemName = row?.findViewById(R.id.text1) as CheckedTextView

        }
    }
}
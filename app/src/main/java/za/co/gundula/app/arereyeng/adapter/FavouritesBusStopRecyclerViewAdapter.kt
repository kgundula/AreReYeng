package za.co.gundula.app.arereyeng.adapter

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import za.co.gundula.app.arereyeng.R
import za.co.gundula.app.arereyeng.utils.Utility

/**
 * Created by kgundula on 2017/03/01.
 */
class FavouritesBusStopRecyclerViewAdapter(private val mContext: Context, private val busStops: Cursor?) : RecyclerView.Adapter<FavouritesBusStopRecyclerViewAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.favourite_bus_stop_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (busStops!!.moveToPosition(position)) {
            val date = Utility.getDateFromISOString(busStops.getString(2))
            val formated_date_time = Utility.formatDate(date) + " " + Utility.formatTime(date)

            holder.bus_stop_name!!.text = busStops.getString(1)
            holder.date_created!!.text = formated_date_time
        }
    }

    override fun getItemCount(): Int {
        return busStops?.count ?: 0
    }

    inner class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        @BindView(R.id.bus_stop_name)
        var bus_stop_name: TextView? = null

        @BindView(R.id.date_created)
        var date_created: TextView? = null

        init {
            ButterKnife.bind(this, itemView!!)
        }
    }

}
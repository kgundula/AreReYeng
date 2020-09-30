package za.co.gundula.app.arereyeng.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import za.co.gundula.app.arereyeng.R
import za.co.gundula.app.arereyeng.model.BusTimeTable
import za.co.gundula.app.arereyeng.utils.Utility

/**
 * Created by kgundula on 2017/02/23.
 */
class BusTimeTableRecyclerAdapter(private val busTimeTables: List<BusTimeTable>?) : RecyclerView.Adapter<BusTimeTableRecyclerAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.timetable_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val busTimeTable = busTimeTables!![position]
        val busLine = busTimeTables[position].busLine
        
        holder.line_color!!.setBackgroundColor(Color.parseColor(busLine.colour))
        holder.line_name!!.text = busLine.name + " - " + busLine.shortName

        val arrivalDate = Utility.getDateFromISOString(busTimeTable.arrivalTime)
        val arrival_date_time = Utility.formatDate(arrivalDate) + " " + Utility.formatTime(arrivalDate)
        val departureDate = Utility.getDateFromISOString(busTimeTable.departureTime)
        val departure_date_time = Utility.formatDate(departureDate) + " " + Utility.formatTime(departureDate)

        val arrival = "Arrival Time : $arrival_date_time"
        holder.arrival_time!!.text = arrival

        val departure = "Departure Time : $departure_date_time"
        holder.depart_time!!.text = departure
    }

    override fun getItemCount(): Int {
        return busTimeTables?.size ?: 0
    }

    inner class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        @BindView(R.id.line_color)
        var line_color: View? = null

        @BindView(R.id.line_name)
        var line_name: TextView? = null

        @BindView(R.id.arrival_time)
        var arrival_time: TextView? = null

        @BindView(R.id.depart_time)
        var depart_time: TextView? = null

        @BindView(R.id.show_line_details)
        var show_line_details: ImageView? = null

        init {
            ButterKnife.bind(this, itemView!!)
        }
    }

}
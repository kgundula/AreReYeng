package za.co.gundula.app.arereyeng.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import za.co.gundula.app.arereyeng.R;
import za.co.gundula.app.arereyeng.model.BusLine;
import za.co.gundula.app.arereyeng.model.BusTimeTable;
import za.co.gundula.app.arereyeng.utils.Utility;

/**
 * Created by kgundula on 2017/02/23.
 */

public class BusTimeTableRecyclerAdapter extends RecyclerView.Adapter<BusTimeTableRecyclerAdapter.ViewHolder> {

    private List<BusTimeTable> busTimeTables;

    public BusTimeTableRecyclerAdapter(List<BusTimeTable> busTimeTables) {
        this.busTimeTables = busTimeTables;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.timetable_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        //Log.i("Ygritte", busTimeTables.get(position).getBusLine().getName());

        BusTimeTable busTimeTable = busTimeTables.get(position);
        BusLine busLine = busTimeTables.get(position).getBusLine();
        holder.line_color.setBackgroundColor(Color.parseColor(busLine.getColour()));
        holder.line_name.setText(busLine.getName() + " - " + busLine.getShortName());

        Date arrivalDate = Utility.getDateFromISOString(busTimeTable.getArrivalTime());
        String arrival_date_time = Utility.formatDate(arrivalDate) + " " + Utility.formatTime(arrivalDate);

        Date departureDate = Utility.getDateFromISOString(busTimeTable.getDepartureTime());
        String departure_date_time = Utility.formatDate(departureDate) + " " + Utility.formatTime(departureDate);

        String arrival = "Arrival Time : " + arrival_date_time;
        holder.arrival_time.setText(arrival);
        String departure = "Departure Time : " + departure_date_time;
        holder.depart_time.setText(departure);


    }

    @Override
    public int getItemCount() {
        return busTimeTables == null ? 0 : busTimeTables.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.line_color)
        View line_color;

        @BindView(R.id.line_name)
        TextView line_name;

        @BindView(R.id.arrival_time)
        TextView arrival_time;

        @BindView(R.id.depart_time)
        TextView depart_time;

        @BindView(R.id.show_line_details)
        ImageView show_line_details;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

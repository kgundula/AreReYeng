package za.co.gundula.app.arereyeng.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import za.co.gundula.app.arereyeng.R;
import za.co.gundula.app.arereyeng.utils.Utility;

/**
 * Created by kgundula on 2017/03/01.
 */

public class FavouritesBusStopRecyclerViewAdapter extends RecyclerView.Adapter<FavouritesBusStopRecyclerViewAdapter.ViewHolder> {

    private Cursor busStops;
    private Context mContext;

    public FavouritesBusStopRecyclerViewAdapter(Context context, Cursor busStops) {
        this.busStops = busStops;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.favourite_bus_stop_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FavouritesBusStopRecyclerViewAdapter.ViewHolder holder, int position) {

        if (busStops.moveToPosition(position)) {

            Date date = Utility.getDateFromISOString(busStops.getString(2));
            String formated_date_time = Utility.formatDate(date) + " " + Utility.formatTime(date);
            holder.bus_stop_name.setText(busStops.getString(1));
            holder.date_created.setText(formated_date_time);

        }
    }

    @Override
    public int getItemCount() {
        return busStops == null ? 0 : busStops.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.bus_stop_name)
        TextView bus_stop_name;

        @BindView(R.id.date_created)
        TextView date_created;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

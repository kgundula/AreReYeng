package za.co.gundula.app.arereyeng.ui;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import za.co.gundula.app.arereyeng.R;
import za.co.gundula.app.arereyeng.adapter.BusTimeTableRecyclerAdapter;
import za.co.gundula.app.arereyeng.data.AreYengContract;
import za.co.gundula.app.arereyeng.model.Agency;
import za.co.gundula.app.arereyeng.model.BusLine;
import za.co.gundula.app.arereyeng.model.BusTimeTable;
import za.co.gundula.app.arereyeng.rest.WhereIsMyTransportApiClient;
import za.co.gundula.app.arereyeng.rest.WhereIsMyTransportApiClientInterface;
import za.co.gundula.app.arereyeng.utils.RecylerViewDividerItemDecoration;
import za.co.gundula.app.arereyeng.utils.Utility;

import static za.co.gundula.app.arereyeng.utils.Constants.agency_key;

public class BusTimetableActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.from_bus_station)
    AutoCompleteTextView from_bus_station;

    @BindView(R.id.info_text)
    TextView info_text;

    @BindView(R.id.bus_stop_timetable)
    RecyclerView bus_stop_timetable;

    public static int COL_STOP_ID = 0;
    public static int COL_STOP_NAME = 1;

    List<String> bus_stop_name = new ArrayList<>();
    List<String> bus_stop_id = new ArrayList<>();

    HashMap<String, String> stopsMap = new HashMap<>();

    Agency agency = null;

    private static final int BUS_STOP_LOADER = 0;
    WhereIsMyTransportApiClientInterface whereIsMyTransportApiClient;
    Context context;
    BusTimeTableRecyclerAdapter busTimeTableRecyclerAdapter;
    List<BusTimeTable> busTimeTableList = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_timetable);
        context = getApplicationContext();
        ButterKnife.bind(this);
        whereIsMyTransportApiClient = WhereIsMyTransportApiClient.getClient(context).create(WhereIsMyTransportApiClientInterface.class);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            agency = bundle.getParcelable(agency_key);
            String stop_id = bundle.getString(BusStopWidget.EXTRA_BUS_STOP_ID);
            String stop_name = bundle.getString(BusStopWidget.EXTRA_BUS_STOP_NAME);

            if (!"".equals(stop_id)) {

                from_bus_station.setText(stop_name);
                getBusTimeTable(stop_id, stop_name);

            }
        }

        if (agency != null) {
            from_bus_station.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    from_bus_station.showDropDown();
                }
            });
        }

        linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        bus_stop_timetable.setLayoutManager(linearLayoutManager);
        bus_stop_timetable.addItemDecoration(new RecylerViewDividerItemDecoration(context));
        bus_stop_timetable.setHasFixedSize(true);

        toolbar.setTitle(getResources().getString(R.string.timetables));
        setSupportActionBar(toolbar);

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportLoaderManager().initLoader(BUS_STOP_LOADER, null, this);

        info_text.setText(R.string.select_bus_stop);

        from_bus_station.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                from_bus_station.showDropDown();
            }
        });

        from_bus_station.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String stop_name = (String) parent.getItemAtPosition(position);
                String stop_id = stopsMap.get(stop_name);
                getBusTimeTable(stop_id, stop_name);
            }
        });

    }


    public void getBusTimeTable(final String stop_id, final String stop_name) {


        Call<ResponseBody> call = whereIsMyTransportApiClient.getStopTimetable(stop_id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful()) {

                    BusTimeTable busTimeTable = null;
                    BusLine busLine = null;
                    try {
                        String timetables = response.body().string();
                        JSONArray time_tables = new JSONArray(timetables);

                        Date now = new Date();
                        String time_iso = Utility.getISOCurrentDateTime(now);

                        ContentValues favouriteBusStopValues = new ContentValues();
                        favouriteBusStopValues.put(AreYengContract.FavoritesBusEntry.COLUMN_ID, stop_id);
                        favouriteBusStopValues.put(AreYengContract.FavoritesBusEntry.COLUMN_NAME, stop_name);
                        favouriteBusStopValues.put(AreYengContract.FavoritesBusEntry.COLUMN_DATE_CREATED, time_iso);

                        getContentResolver().insert(AreYengContract.FavoritesBusEntry.CONTENT_URI, favouriteBusStopValues);
                        getContentResolver().notifyChange(AreYengContract.FavoritesBusEntry.CONTENT_URI, null);

                        busTimeTableList = new ArrayList<BusTimeTable>();
                        for (int i = 0; i < time_tables.length(); i++) {
                            Gson bus_timetable_gson = new Gson();

                            String time_table = time_tables.getString(i);

                            busTimeTable = bus_timetable_gson.fromJson(time_table, BusTimeTable.class);
                            JSONObject busBusTimetableJsonObject = new JSONObject(time_table);
                            String bus_line = busBusTimetableJsonObject.getString("line");
                            busLine = bus_timetable_gson.fromJson(bus_line, BusLine.class);

                            busTimeTable.setBusLine(busLine);
                            busTimeTableList.add(busTimeTable);
                        }

                        busTimeTableRecyclerAdapter = new BusTimeTableRecyclerAdapter(busTimeTableList);
                        busTimeTableRecyclerAdapter.notifyDataSetChanged();
                        bus_stop_timetable.setAdapter(busTimeTableRecyclerAdapter);

                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i("Ygritte", "" + t.getMessage());
            }
        });

    }


    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        Uri bus_stop_uri = AreYengContract.BusStopEntry.CONTENT_URI;

        String[] BUS_STOP_PROJECTION = new String[]{
                AreYengContract.BusStopEntry.COLUMN_ID,
                AreYengContract.BusStopEntry.COLUMN_NAME,
                AreYengContract.BusStopEntry.COLUMN_CODE,
                AreYengContract.BusStopEntry.COLUMN_HREF,
                AreYengContract.BusStopEntry.COLUMN_MODES,
                AreYengContract.BusStopEntry.COLUMN_GEOMETRY_LATITUDE,
                AreYengContract.BusStopEntry.COLUMN_GEOMETRY_LONGITUDE,
                AreYengContract.BusStopEntry.COLUMN_GEOMETRY_TYPE,
        };

        return new CursorLoader(
                this,
                bus_stop_uri,
                BUS_STOP_PROJECTION,
                null,
                null,
                null
        );
    }

    public void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(BUS_STOP_LOADER, null, this);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {

            do {
                //Log.i("Ygritte", DatabaseUtils.dumpCursorToString(data));
                String stop_id = data.getString(COL_STOP_ID);
                String stop_name = data.getString(COL_STOP_NAME);

                stopsMap.put(stop_name, stop_id);
                bus_stop_name.add(stop_name);
                bus_stop_id.add(stop_id);


            } while (data.moveToNext());

            ArrayAdapter<String> station_name = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, bus_stop_name);

            from_bus_station.setAdapter(station_name);
            from_bus_station.setThreshold(0);


        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}

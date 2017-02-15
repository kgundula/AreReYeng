package za.co.gundula.app.arereyeng.ui;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import za.co.gundula.app.arereyeng.R;
import za.co.gundula.app.arereyeng.data.AreYengContract;
import za.co.gundula.app.arereyeng.model.Agency;
import za.co.gundula.app.arereyeng.rest.WhereIsMyTransportApiClient;
import za.co.gundula.app.arereyeng.rest.WhereIsMyTransportApiClientInterface;

import static za.co.gundula.app.arereyeng.utils.Constants.agency_key;

public class BusFareActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.from_bus_station)
    AutoCompleteTextView from_bus_station;

    @BindView(R.id.to_bus_station)
    AutoCompleteTextView to_bus_station;

    Agency agency = null;
    private static final int BUS_STOP_LOADER = 0;

    Cursor bus_stops;

    Uri bus_stop_uri = AreYengContract.BusStopEntry.CONTENT_URI;

    List<String> bus_station_from = new ArrayList<>();
    List<String> bus_station_to = new ArrayList<>();

    String from_busstation = "";
    String to_busstation = "";

    WhereIsMyTransportApiClientInterface whereIsMyTransportApiClient;
    Context context;

    String[] BUS_STOP_PROJECTION = new String[]{
            AreYengContract.BusStopEntry.COLUMN_ID,
            AreYengContract.BusStopEntry.COLUMN_NAME,
            AreYengContract.BusStopEntry.COLUMN_HREF,
            AreYengContract.BusStopEntry.COLUMN_GEOMETRY_LATITUDE,
            AreYengContract.BusStopEntry.COLUMN_GEOMETRY_LONGITUDE,
            AreYengContract.BusStopEntry.COLUMN_GEOMETRY_TYPE,
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_fare);
        ButterKnife.bind(this);

        context = getApplicationContext();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            agency = bundle.getParcelable(agency_key);
        }

        whereIsMyTransportApiClient = WhereIsMyTransportApiClient.getClient(context).create(WhereIsMyTransportApiClientInterface.class);

        // ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, COUNTRIES);
        toolbar.setTitle(getResources().getString(R.string.fare_calculator));
        setSupportActionBar(toolbar);

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Log.i("Ygritte", "Id " + agency.getId());
        getFareProducts();

    }

    public void getFareProducts() {

        Call<ResponseBody> call = whereIsMyTransportApiClient.getFareProducts(agency.getId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                String response_string = null;

                try {
                    if (response != null) {

                        response_string = response.errorBody().string();
                        Log.i("Ygritte", response_string);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i("Ygritte", t.getMessage());
            }
        });
    }

    public void onResume() {
        super.onResume();

        bus_stops = getContentResolver().query(bus_stop_uri, BUS_STOP_PROJECTION, null, null, null);

        if (bus_stops != null && bus_stops.moveToFirst()) {

            do {
                String station_name = bus_stops.getString(1);
                bus_station_from.add(station_name);
                bus_station_to.add(station_name);

            } while (bus_stops.moveToNext());

        }

        ArrayAdapter<String> from_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, bus_station_from);
        ArrayAdapter<String> to_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, bus_station_to);

        from_bus_station.setAdapter(from_adapter);
        from_bus_station.setThreshold(0);
        from_bus_station.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                from_bus_station.showDropDown();
            }
        });

        from_bus_station.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                from_busstation = (String) parent.getItemAtPosition(position);
                searchBusFare();
            }
        });

        to_bus_station.setAdapter(to_adapter);
        to_bus_station.setThreshold(0);
        to_bus_station.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                to_bus_station.showDropDown();
            }
        });

        to_bus_station.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                to_busstation = (String) parent.getItemAtPosition(position);
                searchBusFare();
            }
        });

    }

    public void searchBusFare() {
        if (!"".equals(from_busstation) && !"".equals(to_busstation)) {
            Log.i("Ygritte", " From - " + from_busstation + "  : To -" + to_busstation);
        } else {
            Log.i("Ygritte", "Both are required");
        }
    }
}

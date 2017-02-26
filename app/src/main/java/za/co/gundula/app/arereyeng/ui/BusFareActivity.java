package za.co.gundula.app.arereyeng.ui;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

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
import za.co.gundula.app.arereyeng.model.FareProduct;
import za.co.gundula.app.arereyeng.rest.WhereIsMyTransportApiClient;
import za.co.gundula.app.arereyeng.rest.WhereIsMyTransportApiClientInterface;
import za.co.gundula.app.arereyeng.utils.Constants;

import static za.co.gundula.app.arereyeng.utils.Constants.agency_key;

public class BusFareActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.from_bus_station)
    AutoCompleteTextView from_bus_station;

    @BindView(R.id.to_bus_station)
    AutoCompleteTextView to_bus_station;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.linear_detail)
    LinearLayout linear_detail;

    @BindView(R.id.journey_time)
    TextView journey_time;

    @BindView(R.id.station_recycler_view)
    RecyclerView station_recycler_view;

    @BindView(R.id.info_text)
    TextView info_text;

    Agency agency = null;

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

    SharedPreferences mSharedPref;
    SharedPreferences.Editor mSharedPrefEditor;

    boolean is_are_product_already_saved = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_fare);
        ButterKnife.bind(this);

        context = getApplicationContext();
        mSharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        mSharedPrefEditor = mSharedPref.edit();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            agency = bundle.getParcelable(agency_key);
        }

        info_text.setText(R.string.search_for);

        whereIsMyTransportApiClient = WhereIsMyTransportApiClient.getClient(context).create(WhereIsMyTransportApiClientInterface.class);

        toolbar.setTitle(getResources().getString(R.string.fare_calculator));
        setSupportActionBar(toolbar);

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        is_are_product_already_saved = mSharedPref.getBoolean(Constants.fare_product_already_saved, false);

        if (!is_are_product_already_saved) {

        }
        if (agency != null) {
            getFareProducts();
        }

    }

    public void getFareProducts() {

        Log.i("Ygritte", agency.getId());
        Call<ResponseBody> call = whereIsMyTransportApiClient.getFareProducts(agency.getId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {


                if (response.isSuccessful()) {

                    String fareproducts = null;
                    try {
                        fareproducts = response.body().string();
                        JSONArray fare_products = new JSONArray(fareproducts);

                        FareProduct fareProduct = new Gson().fromJson(fare_products.getString(0), FareProduct.class);

                        Log.i("Ygritte", "Fare Name : " + fareProduct.getName());

                        ContentValues fareProductValues = new ContentValues();
                        fareProductValues.put(AreYengContract.FareProductEntry.COLUMN_ID, fareProduct.getId());
                        fareProductValues.put(AreYengContract.FareProductEntry.COLUMN_NAME, fareProduct.getName());
                        fareProductValues.put(AreYengContract.FareProductEntry.COLUMN_HREF, fareProduct.getHref());
                        fareProductValues.put(AreYengContract.FareProductEntry.COLUMN_IS_DEFAULT, fareProduct.isDefault());
                        fareProductValues.put(AreYengContract.FareProductEntry.COLUMN_AGENCY_ID, agency.getId());

                        try {
                            getContentResolver().insert(AreYengContract.FareProductEntry.CONTENT_URI, fareProductValues);
                            getContentResolver().notifyChange(AreYengContract.FareProductEntry.CONTENT_URI, null);
                            mSharedPrefEditor.putBoolean(Constants.fare_product_already_saved, true).apply();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

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
            //getFareProducts();

        } else {
            Log.i("Ygritte", "Both are required");
        }
    }
}

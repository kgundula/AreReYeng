package za.co.gundula.app.arereyeng.ui;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
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
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

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
import za.co.gundula.app.arereyeng.model.Geometry;
import za.co.gundula.app.arereyeng.model.Journey;
import za.co.gundula.app.arereyeng.rest.WhereIsMyTransportApiClient;
import za.co.gundula.app.arereyeng.rest.WhereIsMyTransportApiClientInterface;
import za.co.gundula.app.arereyeng.utils.Constants;
import za.co.gundula.app.arereyeng.utils.Utility;

import static za.co.gundula.app.arereyeng.utils.Constants.agency_key;
import static za.co.gundula.app.arereyeng.utils.Constants.fare_product_already_saved;

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

    @BindView(R.id.journey_detail)
    TextView detail_journey;

    @BindView(R.id.journey_cost)
    TextView journey_cost;

    @BindView(R.id.journey_distance)
    TextView journey_distance;

    @BindView(R.id.journey_duration)
    TextView journey_duration;

    @BindView(R.id.fare_type)
    TextView fare_type;

    @BindView(R.id.info_text)
    TextView info_text;

    @BindView(R.id.share_journey_info)
    FloatingActionButton share_journey_info;

    @BindView(R.id.error_message)
    TextView error_message;

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

    HashMap<String, String> stopLatitudeMap = new HashMap<>();
    HashMap<String, String> stopLongitudeMap = new HashMap<>();


    boolean is_are_product_already_saved = false;
    String agency_id = "";

    public final static String itineraries_key = "itineraries";
    public final static String legs_key = "legs";
    public final static String fare_key = "fare";
    public final static String distance_key = "distance";
    public final static String description_key = "description";
    public final static String amount_key = "amount";
    public final static String cost_key = "cost";
    public final static String type_key = "type";
    public final static String value_key = "value";
    public final static String unit_key = "unit";
    public final static String duration_key = "duration";

    public String share_info = "";
    public String share_title = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_fare);
        ButterKnife.bind(this);

        context = getApplicationContext();
        whereIsMyTransportApiClient = WhereIsMyTransportApiClient.getClient(context).create(WhereIsMyTransportApiClientInterface.class);

        mSharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        mSharedPrefEditor = mSharedPref.edit();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            agency = bundle.getParcelable(agency_key);
        }

        info_text.setText(R.string.search_for);


        toolbar.setTitle(getResources().getString(R.string.fare_calculator));
        setSupportActionBar(toolbar);

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        is_are_product_already_saved = mSharedPref.getBoolean(fare_product_already_saved, false);
        agency_id = mSharedPref.getString(Constants.agency_id, "");
        if (!"".equals(agency_id)) {
            getFareProducts(agency_id);
        }

        share_journey_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(BusFareActivity.this)
                        .setType("text/plain")
                        .setText(share_info)
                        .getIntent(), share_title));
            }
        });

    }

    public void getFareProducts(String agency_id) {

        Call<ResponseBody> call = whereIsMyTransportApiClient.getFareProducts(agency_id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {


                if (response.isSuccessful()) {

                    String fareproducts = null;
                    try {
                        fareproducts = response.body().string();
                        JSONArray fare_products = new JSONArray(fareproducts);

                        FareProduct fareProduct = new Gson().fromJson(fare_products.getString(0), FareProduct.class);

                        ContentValues fareProductValues = new ContentValues();
                        fareProductValues.put(AreYengContract.FareProductEntry.COLUMN_ID, fareProduct.getId());
                        fareProductValues.put(AreYengContract.FareProductEntry.COLUMN_NAME, fareProduct.getName());
                        fareProductValues.put(AreYengContract.FareProductEntry.COLUMN_HREF, fareProduct.getHref());
                        fareProductValues.put(AreYengContract.FareProductEntry.COLUMN_IS_DEFAULT, fareProduct.isDefault());
                        fareProductValues.put(AreYengContract.FareProductEntry.COLUMN_AGENCY_ID, agency.getId());

                        try {
                            getContentResolver().insert(AreYengContract.FareProductEntry.CONTENT_URI, fareProductValues);
                            getContentResolver().notifyChange(AreYengContract.FareProductEntry.CONTENT_URI, null);
                            mSharedPrefEditor.putBoolean(fare_product_already_saved, true).apply();

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
                Log.i("Ygritte", "" + t.getMessage());
            }
        });
    }

    public void onResume() {
        super.onResume();

        bus_stops = getContentResolver().query(bus_stop_uri, BUS_STOP_PROJECTION, null, null, null);

        if (bus_stops != null && bus_stops.moveToFirst()) {

            do {
                String station_name = bus_stops.getString(1);
                String station_latitude = bus_stops.getString(3);
                String station_longitude = bus_stops.getString(4);
                bus_station_from.add(station_name);
                bus_station_to.add(station_name);
                stopLatitudeMap.put(station_name, station_latitude);
                stopLongitudeMap.put(station_name, station_longitude);

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

            if (from_bus_station.equals(to_bus_station)) {
                showErrorView(getString(R.string.same_stations));
            } else {
                Cursor cursor = getFareProduct();
                if (cursor != null && cursor.moveToFirst()) {
                    progressBar.setIndeterminate(true);
                    progressBar.setVisibility(View.VISIBLE);

                    double from_lat = Double.valueOf(stopLatitudeMap.get(from_busstation));
                    double from_longi = Double.valueOf(stopLongitudeMap.get(from_busstation));

                    double to_lat = Double.valueOf(stopLatitudeMap.get(to_busstation));
                    double to_longi = Double.valueOf(stopLongitudeMap.get(to_busstation));

                    List<Double> fromCoordinates = new ArrayList<>();
                    fromCoordinates.add(from_longi);
                    fromCoordinates.add(from_lat);

                    List<Double> toCoordinates = new ArrayList<>();
                    toCoordinates.add(to_longi);
                    toCoordinates.add(to_lat);

                    List<List<Double>> coordinates = new ArrayList<>();
                    coordinates.add(fromCoordinates);
                    coordinates.add(toCoordinates);

                    Geometry geometry = new Geometry("MultiPoint", coordinates);

                    List<String> fareProducts = new ArrayList<>();
                    fareProducts.add(cursor.getString(0));

                    Date now = new Date();
                    String time_iso = Utility.getISOCurrentDateTime(now);

                    Journey journey = new Journey(geometry, time_iso, "DepartAfter", fareProducts);
                    String journeyPost = new Gson().toJson(journey);
                    Call<ResponseBody> call = whereIsMyTransportApiClient.calculateJourneyFare(journey);
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                            if (response.isSuccessful()) {

                                try {
                                    double fare_cost = 0.0;
                                    int itenerary_distance = 0;
                                    int itenerary_duration = 0;
                                    String itenarary_duration_unit = "m";
                                    String fare_description = "";

                                    String response_string = response.body().string();
                                    JSONObject journeyObject = new JSONObject(response_string);
                                    // Get Itineraries
                                    String itinerary = journeyObject.getString(itineraries_key);
                                    JSONArray itinerariesArray = new JSONArray(itinerary);
                                    for (int i = 0; i < itinerariesArray.length(); i++) {
                                        JSONObject itineraryObject = (JSONObject) itinerariesArray.get(i);
                                        JSONObject distance = itineraryObject.getJSONObject(distance_key);

                                        itenerary_distance += distance.getInt(value_key);
                                        itenarary_duration_unit = distance.getString(unit_key);

                                        itenerary_duration += itineraryObject.getInt(duration_key);

                                        if (itineraryObject.has(legs_key)) {

                                            String legs = itineraryObject.getString(legs_key);
                                            JSONArray legsArray = new JSONArray(legs);

                                            for (int x = 0; x < legsArray.length(); x++) {
                                                JSONObject leg = (JSONObject) legsArray.get(x);
                                                String transit_type = leg.getString(type_key);
                                                if ("Transit".equals(transit_type)) {

                                                    JSONObject fareObject = leg.getJSONObject(fare_key);
                                                    JSONObject costObject = fareObject.getJSONObject(cost_key);
                                                    fare_cost += costObject.getDouble(amount_key);
                                                    fare_description = fareObject.getString(description_key);
                                                }
                                                //"fare":{"description":"Default fare","fareProduct":{"id":"fSlKJlDHKEiGXPwXwUu_rA","href":"https:\/\/platform.whereismytransport.com\/api\/fareproducts\/fSlKJlDHKEiGXPwXwUu_rA","agency":{"id":"A1JHSPIg_kWV5XRHIepCLw","href":"https:\/\/platform.whereismytransport.com\/api\/agencies\/A1JHSPIg_kWV5XRHIepCLw","name":"A Re Yeng","culture":"en"},"name":"Standard","isDefault":true},"cost":{"amount":9.5,"currencyCode":"ZAR"},"messages":[]}
                                            }
                                        }
                                    }
                                    showJourneyFare(fare_cost, itenerary_distance, itenerary_duration, itenarary_duration_unit, fare_description);

                                } catch (IOException e) {
                                    showErrorView(e.getMessage());
                                } catch (JSONException e) {
                                    showErrorView(e.getMessage());
                                }

                            } else {
                                showErrorView(response.raw().toString());
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            showErrorView(t.getMessage());
                        }
                    });
                }
            }
        }
    }


    public void showErrorView(String message) {

        progressBar.setVisibility(View.GONE);
        error_message.setText(message);
        error_message.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        error_message.setVisibility(View.VISIBLE);
        linear_detail.setVisibility(View.GONE);
    }

    /*
    double fare_cost = 0.0;
    int itenerary_distance = 0;
    int itenerary_duration = 0;
    String itenarary_duration_unit = "m";
    String fare_description = "";
     */
    public void showJourneyFare(double cost, int itenerary_distance, int itenerary_duration, String itenarary_duration_unit, String fare_description) {

        error_message.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        linear_detail.setVisibility(View.VISIBLE);

        String journey_from_to = from_busstation + " to " + to_busstation;
        detail_journey.setText(journey_from_to);


        String j_cost = String.format(Locale.ENGLISH, "%10.2f", cost);
        journey_cost.setText(j_cost);

        String distance_km = String.valueOf(itenerary_distance / 1000);
        String estimated_distance = distance_km + " KM";
        journey_distance.setText(estimated_distance);


        int num_hours = (itenerary_duration % 86400) / 3600;
        int num_minutes = ((itenerary_duration % 86400) % 3600) / 60;
        int num_seconds = ((itenerary_duration % 86400) % 3600) % 60;

        String estimated_travel_time = num_hours + getString(R.string.hours) + num_minutes + getString(R.string.minutes) + num_seconds + getString(R.string.seconds);
        share_info += "\n" + journey_from_to;
        share_info += "\n\n" + estimated_distance;
        share_info += "\n" + estimated_travel_time;
        share_info += "\n" + estimated_travel_time;

        share_title = journey_from_to;
        journey_duration.setText(estimated_travel_time);
        fare_type.setText(j_cost);


        share_journey_info.setVisibility(View.VISIBLE);


    }


    public Cursor getFareProduct() {

        String[] FARE_PROJECTION = new String[]{
                AreYengContract.FareProductEntry.COLUMN_ID,
                AreYengContract.FareProductEntry.COLUMN_NAME,
                AreYengContract.FareProductEntry.COLUMN_HREF,
                AreYengContract.FareProductEntry.COLUMN_IS_DEFAULT,
                AreYengContract.FareProductEntry.COLUMN_AGENCY_ID,
        };

        return getContentResolver().query(
                AreYengContract.FareProductEntry.CONTENT_URI,
                FARE_PROJECTION,
                null,
                null,
                null);
    }

    public void busFareErrorView() {
        //SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        //@StockTaskService.StockStatus int stockStatus = sp.getInt(getString(R.string.stock_status), -1);
        //String message = getString(R.string.empty_data) + " ";
    }
}

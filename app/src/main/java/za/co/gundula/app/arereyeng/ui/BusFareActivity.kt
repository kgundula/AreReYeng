package za.co.gundula.app.arereyeng.ui

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import za.co.gundula.app.arereyeng.R
import za.co.gundula.app.arereyeng.data.AreYengContract
import za.co.gundula.app.arereyeng.model.FareProduct
import za.co.gundula.app.arereyeng.model.Geometry
import za.co.gundula.app.arereyeng.model.Journey
import za.co.gundula.app.arereyeng.rest.WhereIsMyTransportApiClient.getClient
import za.co.gundula.app.arereyeng.rest.WhereIsMyTransportApiClientInterface
import za.co.gundula.app.arereyeng.ui.BusFareActivity
import za.co.gundula.app.arereyeng.utils.Constants
import za.co.gundula.app.arereyeng.utils.Utility
import java.io.IOException
import java.util.*

class BusFareActivity : AppCompatActivity() {
    @BindView(R.id.toolbar)
    var toolbar: Toolbar? = null

    @BindView(R.id.from_bus_station)
    var from_bus_station: AutoCompleteTextView? = null

    @BindView(R.id.to_bus_station)
    var to_bus_station: AutoCompleteTextView? = null

    @BindView(R.id.progressBar)
    var progressBar: ProgressBar? = null

    @BindView(R.id.linear_detail)
    var linear_detail: LinearLayout? = null

    @BindView(R.id.journey_detail)
    var detail_journey: TextView? = null

    @BindView(R.id.journey_cost)
    var journey_cost: TextView? = null

    @BindView(R.id.journey_distance)
    var journey_distance: TextView? = null

    @BindView(R.id.journey_duration)
    var journey_duration: TextView? = null

    @BindView(R.id.fare_type)
    var fare_type: TextView? = null

    @BindView(R.id.info_text)
    var info_text: TextView? = null

    @BindView(R.id.share_journey_info)
    var share_journey_info: FloatingActionButton? = null

    @BindView(R.id.error_message)
    var error_message: TextView? = null
    var agency: Agency? = null
    var bus_stops: Cursor? = null
    var bus_stop_uri = AreYengContract.BusStopEntry.CONTENT_URI
    var bus_station_from: MutableList<String> = ArrayList()
    var bus_station_to: MutableList<String> = ArrayList()
    var from_busstation = ""
    var to_busstation = ""
    var whereIsMyTransportApiClient: WhereIsMyTransportApiClientInterface? = null
    var context: Context? = null
    var BUS_STOP_PROJECTION = arrayOf(
            AreYengContract.BusStopEntry.COLUMN_ID,
            AreYengContract.BusStopEntry.COLUMN_NAME,
            AreYengContract.BusStopEntry.COLUMN_HREF,
            AreYengContract.BusStopEntry.COLUMN_GEOMETRY_LATITUDE,
            AreYengContract.BusStopEntry.COLUMN_GEOMETRY_LONGITUDE,
            AreYengContract.BusStopEntry.COLUMN_GEOMETRY_TYPE)
    var mSharedPref: SharedPreferences? = null
    var mSharedPrefEditor: SharedPreferences.Editor? = null
    var stopLatitudeMap = HashMap<String, String>()
    var stopLongitudeMap = HashMap<String, String>()
    var is_are_product_already_saved = false
    var agency_id: String? = ""
    var share_info = ""
    var share_title = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bus_fare)
        ButterKnife.bind(this)
        context = applicationContext
        whereIsMyTransportApiClient = getClient(context)!!.create(WhereIsMyTransportApiClientInterface::class.java)
        mSharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        mSharedPrefEditor = mSharedPref.edit()
        val bundle = intent.extras
        if (bundle != null) {
            agency = bundle.getParcelable(Constants.agency_key)
        }
        info_text!!.setText(R.string.search_for)
        toolbar!!.title = resources.getString(R.string.fare_calculator)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        is_are_product_already_saved = mSharedPref.getBoolean(Constants.fare_product_already_saved, false)
        agency_id = mSharedPref.getString(Constants.agency_id, "")
        if ("" != agency_id) {
            getFareProducts(agency_id)
        }
        share_journey_info!!.setOnClickListener {
            startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(this@BusFareActivity)
                    .setType("text/plain")
                    .setText(share_info)
                    .intent, share_title))
        }
    }

    fun getFareProducts(agency_id: String?) {
        val call = whereIsMyTransportApiClient!!.getFareProducts(agency_id)
        call!!.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    var fareproducts: String? = null
                    try {
                        fareproducts = response.body()!!.string()
                        val fare_products = JSONArray(fareproducts)
                        val fareProduct = Gson().fromJson(fare_products.getString(0), FareProduct::class.java)
                        val fareProductValues = ContentValues()
                        fareProductValues.put(AreYengContract.FareProductEntry.COLUMN_ID, fareProduct.id)
                        fareProductValues.put(AreYengContract.FareProductEntry.COLUMN_NAME, fareProduct.name)
                        fareProductValues.put(AreYengContract.FareProductEntry.COLUMN_HREF, fareProduct.href)
                        fareProductValues.put(AreYengContract.FareProductEntry.COLUMN_IS_DEFAULT, fareProduct.isDefault)
                        fareProductValues.put(AreYengContract.FareProductEntry.COLUMN_AGENCY_ID, agency.id)
                        try {
                            contentResolver.insert(AreYengContract.FareProductEntry.CONTENT_URI, fareProductValues)
                            contentResolver.notifyChange(AreYengContract.FareProductEntry.CONTENT_URI, null)
                            mSharedPrefEditor!!.putBoolean(Constants.fare_product_already_saved, true).apply()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.i("Ygritte", "" + t.message)
            }
        })
    }

    public override fun onResume() {
        super.onResume()
        bus_stops = contentResolver.query(bus_stop_uri, BUS_STOP_PROJECTION, null, null, null)
        if (bus_stops != null && bus_stops!!.moveToFirst()) {
            do {
                val station_name = bus_stops!!.getString(1)
                val station_latitude = bus_stops!!.getString(3)
                val station_longitude = bus_stops!!.getString(4)
                bus_station_from.add(station_name)
                bus_station_to.add(station_name)
                stopLatitudeMap[station_name] = station_latitude
                stopLongitudeMap[station_name] = station_longitude
            } while (bus_stops!!.moveToNext())
        }
        val from_adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, bus_station_from)
        val to_adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, bus_station_to)
        from_bus_station!!.setAdapter(from_adapter)
        from_bus_station!!.threshold = 0
        from_bus_station!!.onFocusChangeListener = OnFocusChangeListener { v, hasFocus -> from_bus_station!!.showDropDown() }
        from_bus_station!!.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            from_busstation = parent.getItemAtPosition(position) as String
            searchBusFare()
        }
        to_bus_station!!.setAdapter(to_adapter)
        to_bus_station!!.threshold = 0
        to_bus_station!!.onFocusChangeListener = OnFocusChangeListener { v, hasFocus -> to_bus_station!!.showDropDown() }
        to_bus_station!!.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            to_busstation = parent.getItemAtPosition(position) as String
            searchBusFare()
        }
    }

    fun searchBusFare() {
        if ("" != from_busstation && "" != to_busstation) {
            if (from_bus_station == to_bus_station) {
                showErrorView(getString(R.string.same_stations))
            } else {
                val cursor = fareProduct
                if (cursor != null && cursor.moveToFirst()) {
                    progressBar!!.isIndeterminate = true
                    progressBar!!.visibility = View.VISIBLE
                    val from_lat = java.lang.Double.valueOf(stopLatitudeMap[from_busstation]!!)
                    val from_longi = java.lang.Double.valueOf(stopLongitudeMap[from_busstation]!!)
                    val to_lat = java.lang.Double.valueOf(stopLatitudeMap[to_busstation]!!)
                    val to_longi = java.lang.Double.valueOf(stopLongitudeMap[to_busstation]!!)
                    val fromCoordinates: MutableList<Double> = ArrayList()
                    fromCoordinates.add(from_longi)
                    fromCoordinates.add(from_lat)
                    val toCoordinates: MutableList<Double> = ArrayList()
                    toCoordinates.add(to_longi)
                    toCoordinates.add(to_lat)
                    val coordinates: MutableList<List<Double>> = ArrayList()
                    coordinates.add(fromCoordinates)
                    coordinates.add(toCoordinates)
                    val geometry = Geometry("MultiPoint", coordinates)
                    val fareProducts: MutableList<String> = ArrayList()
                    fareProducts.add(cursor.getString(0))
                    val now = Date()
                    val time_iso = Utility.getISOCurrentDateTime(now)
                    val journey = Journey(geometry, time_iso, "DepartAfter", fareProducts)
                    val journeyPost = Gson().toJson(journey)
                    val call = whereIsMyTransportApiClient!!.calculateJourneyFare(journey)

                    call!!.enqueue(object : Callback<ResponseBody> {
                        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                            if (response.isSuccessful) {
                                try {
                                    var fare_cost = 0.0
                                    var itenerary_distance = 0
                                    var itenerary_duration = 0
                                    var itenarary_duration_unit = "m"
                                    var fare_description = ""
                                    val response_string = response.body()!!.string()
                                    val journeyObject = JSONObject(response_string)
                                    // Get Itineraries
                                    val itinerary = journeyObject.getString(itineraries_key)
                                    val itinerariesArray = JSONArray(itinerary)
                                    for (i in 0 until itinerariesArray.length()) {
                                        val itineraryObject = itinerariesArray[i] as JSONObject
                                        val distance = itineraryObject.getJSONObject(distance_key)
                                        itenerary_distance += distance.getInt(value_key)
                                        itenarary_duration_unit = distance.getString(unit_key)
                                        itenerary_duration += itineraryObject.getInt(duration_key)
                                        if (itineraryObject.has(legs_key)) {
                                            val legs = itineraryObject.getString(legs_key)
                                            val legsArray = JSONArray(legs)
                                            for (x in 0 until legsArray.length()) {
                                                val leg = legsArray[x] as JSONObject
                                                val transit_type = leg.getString(type_key)
                                                if ("Transit" == transit_type) {
                                                    val fareObject = leg.getJSONObject(fare_key)
                                                    val costObject = fareObject.getJSONObject(cost_key)
                                                    fare_cost += costObject.getDouble(amount_key)
                                                    fare_description = fareObject.getString(description_key)
                                                }
                                                //"fare":{"description":"Default fare","fareProduct":{"id":"fSlKJlDHKEiGXPwXwUu_rA","href":"https:\/\/platform.whereismytransport.com\/api\/fareproducts\/fSlKJlDHKEiGXPwXwUu_rA","agency":{"id":"A1JHSPIg_kWV5XRHIepCLw","href":"https:\/\/platform.whereismytransport.com\/api\/agencies\/A1JHSPIg_kWV5XRHIepCLw","name":"A Re Yeng","culture":"en"},"name":"Standard","isDefault":true},"cost":{"amount":9.5,"currencyCode":"ZAR"},"messages":[]}
                                            }
                                        }
                                    }
                                    showJourneyFare(fare_cost, itenerary_distance, itenerary_duration, itenarary_duration_unit, fare_description)
                                } catch (e: IOException) {
                                    showErrorView(e.message)
                                } catch (e: JSONException) {
                                    showErrorView(e.message)
                                }
                            } else {
                                showErrorView(response.raw().toString())
                            }
                        }

                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            showErrorView(t.message)
                        }
                    })
                }
            }
        }
    }

    fun showErrorView(message: String?) {
        progressBar!!.visibility = View.GONE
        error_message!!.text = message
        error_message!!.setTextColor(ContextCompat.getColor(context!!, R.color.colorAccent))
        error_message!!.visibility = View.VISIBLE
        linear_detail!!.visibility = View.GONE
    }

    /*
    double fare_cost = 0.0;
    int itenerary_distance = 0;
    int itenerary_duration = 0;
    String itenarary_duration_unit = "m";
    String fare_description = "";
     */
    fun showJourneyFare(cost: Double, itenerary_distance: Int, itenerary_duration: Int, itenarary_duration_unit: String?, fare_description: String?) {
        error_message!!.visibility = View.GONE
        progressBar!!.visibility = View.GONE
        linear_detail!!.visibility = View.VISIBLE
        val journey_from_to = "$from_busstation to $to_busstation"
        detail_journey!!.text = journey_from_to
        val j_cost = String.format(Locale.ENGLISH, "%10.2f", cost)
        journey_cost!!.text = j_cost
        val distance_km = (itenerary_distance / 1000).toString()
        val estimated_distance = "$distance_km KM"
        journey_distance!!.text = estimated_distance
        val num_hours = itenerary_duration % 86400 / 3600
        val num_minutes = itenerary_duration % 86400 % 3600 / 60
        val num_seconds = itenerary_duration % 86400 % 3600 % 60
        val estimated_travel_time = num_hours.toString() + " " + getString(R.string.hours) + num_minutes + " " + getString(R.string.minutes) + num_seconds + " " + getString(R.string.seconds)
        share_info += """

            $journey_from_to
            """.trimIndent()
        share_info += """


            $estimated_distance
            """.trimIndent()
        share_info += """

            $estimated_travel_time
            """.trimIndent()
        share_info += """

            $estimated_travel_time
            """.trimIndent()
        share_title = journey_from_to
        journey_duration!!.text = estimated_travel_time
        fare_type!!.text = fare_description
        share_journey_info!!.visibility = View.VISIBLE
    }

    val fareProduct: Cursor?
        get() {
            val FARE_PROJECTION = arrayOf(
                    AreYengContract.FareProductEntry.COLUMN_ID,
                    AreYengContract.FareProductEntry.COLUMN_NAME,
                    AreYengContract.FareProductEntry.COLUMN_HREF,
                    AreYengContract.FareProductEntry.COLUMN_IS_DEFAULT,
                    AreYengContract.FareProductEntry.COLUMN_AGENCY_ID)
            return contentResolver.query(
                    AreYengContract.FareProductEntry.CONTENT_URI,
                    FARE_PROJECTION,
                    null,
                    null,
                    null)
        }

    fun busFareErrorView() {
        //SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        //@StockTaskService.StockStatus int stockStatus = sp.getInt(getString(R.string.stock_status), -1);
        //String message = getString(R.string.empty_data) + " ";
    }

    companion object {
        const val itineraries_key = "itineraries"
        const val legs_key = "legs"
        const val fare_key = "fare"
        const val distance_key = "distance"
        const val description_key = "description"
        const val amount_key = "amount"
        const val cost_key = "cost"
        const val type_key = "type"
        const val value_key = "value"
        const val unit_key = "unit"
        const val duration_key = "duration"
    }
}
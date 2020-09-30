package za.co.gundula.app.arereyeng.ui

import android.appwidget.AppWidgetManager
import android.content.*
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindInt
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import okhttp3.ResponseBody
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import za.co.gundula.app.arereyeng.R
import za.co.gundula.app.arereyeng.R.id
import za.co.gundula.app.arereyeng.adapter.FavouritesBusStopRecyclerViewAdapter
import za.co.gundula.app.arereyeng.data.AreYengContract
import za.co.gundula.app.arereyeng.model.Agency
import za.co.gundula.app.arereyeng.rest.WhereIsMyTransportApiClient.getClient
import za.co.gundula.app.arereyeng.rest.WhereIsMyTransportApiClientInterface
import za.co.gundula.app.arereyeng.sync.AreYengSyncAdapter.Companion.initializeSyncAdapter
import za.co.gundula.app.arereyeng.utils.CircleTransform
import za.co.gundula.app.arereyeng.utils.Constants
import za.co.gundula.app.arereyeng.utils.RecylerViewDividerItemDecoration
import za.co.gundula.app.arereyeng.utils.Utility
import java.util.*

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor?> {
    @BindView(id.toolbar)
    var toolbar: Toolbar? = null

    @BindView(id.drawer_layout)
    var drawer: DrawerLayout? = null

    @BindView(id.content_main)
    var coordinatorLayout: CoordinatorLayout? = null

    @BindView(id.nav_view)
    var navigationView: NavigationView? = null

    @BindView(id.info_text)
    var infoText: TextView? = null

    @BindView(id.bus_stop_favourites)
    var busStopFavourites: RecyclerView? = null

    private var mSharedPref: SharedPreferences? = null
    var mSharedPrefEditor: SharedPreferences.Editor? = null

    @BindInt(R.integer.num_columns)
    var columns = 0
    var context: Context? = null

    private var is_agency_already_saved = false
    private var gridLayoutManager: GridLayoutManager? = null
    private var favouritesBusStopRecyclerViewAdapter: FavouritesBusStopRecyclerViewAdapter? = null
    private var whereIsMyTransportApiClient: WhereIsMyTransportApiClientInterface? = null
    lateinit var agency: Array<Agency>
    private var agency_intent: Agency? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        context = applicationContext
        ButterKnife.bind(this)
        toolbar!!.setSubtitle(R.string.unofficial)
        setSupportActionBar(toolbar)
        mSharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        mSharedPrefEditor = mSharedPref.edit()
        infoText!!.setText(R.string.favourites)
        val toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer!!.addDrawerListener(toggle)
        toggle.syncState()
        navigationView!!.setNavigationItemSelectedListener(this)
        initializeSyncAdapter(this)
        updateUserDetails()
        whereIsMyTransportApiClient = getClient(context)!!.create(WhereIsMyTransportApiClientInterface::class.java)
        is_agency_already_saved = mSharedPref.getBoolean(Constants.agency_already_saved, false)
        if (!is_agency_already_saved) {
            //Agency doesn't change so we only fetch once.
            getAgency()
        } else {
            supportLoaderManager.initLoader(AGENCY_LOADER, null, this)
        }
        supportLoaderManager.initLoader(FAVOURITE_LOADER, null, this)
        val networkInfo = Utility.getNetworkWorkInfo(context)
        if (networkInfo != null && !networkInfo.isAvailable) {
            showSnackBar(getString(R.string.no_network_error))
        } else {
            buses
        }
        val columnCount = columns
        gridLayoutManager = GridLayoutManager(context, columnCount)
        busStopFavourites!!.layoutManager = gridLayoutManager
        busStopFavourites!!.addItemDecoration(RecylerViewDividerItemDecoration(context))
        busStopFavourites!!.setHasFixedSize(true)
    }

    fun showSnackBar(message: String?) {
        Snackbar.make(coordinatorLayout!!, message!!, Snackbar.LENGTH_LONG).show()
    }

    fun getAgency() {
        val call = whereIsMyTransportApiClient!!.agency
        call!!.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                try {
                    //
                    val json_response = response.body()!!.string()
                    // The API returns an array instead of a json object hence we make an agenct array
                    agency = Gson().fromJson(json_response, Array<Agency>::class.java)
                    //agency_intent = agency[0];
                    val agencyValues = ContentValues()
                    agencyValues.put(AreYengContract.AgencyEntry.COLUMN_ID, agency[0].id)
                    agencyValues.put(AreYengContract.AgencyEntry.COLUMN_NAME, agency[0].name)
                    agencyValues.put(AreYengContract.AgencyEntry.COLUMN_HREF, agency[0].href)
                    agencyValues.put(AreYengContract.AgencyEntry.COLUMN_CULTURE, agency[0].culture)

                    //Save Agency to database;
                    try {
                        contentResolver.insert(AreYengContract.AgencyEntry.CONTENT_URI, agencyValues)
                        contentResolver.notifyChange(AreYengContract.AgencyEntry.CONTENT_URI, null)
                        mSharedPrefEditor!!.putBoolean(Constants.agency_already_saved, true).apply()
                        mSharedPrefEditor!!.putString(Constants.agency_id, agency[0].id).apply()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {}
        })
    }

    //Log.i("Ygritte", json_response);
    /*
    * The API returns an array instead of a json object hence we make an agenct array
    * agency = new Gson().fromJson(json_response, Agency[].class);
    */
    val buses:
            // add to database
            Unit
        get() {
            val call = whereIsMyTransportApiClient!!.allBusStops
            call!!.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    try {
                        val json_response = response.body()!!.string()
                        //Log.i("Ygritte", json_response);
                        /*
                        * The API returns an array instead of a json object hence we make an agenct array
                        * agency = new Gson().fromJson(json_response, Agency[].class);
                        */
                        val bus_stops = JSONArray(json_response)
                        val cVVector = Vector<ContentValues>(bus_stops.length())
                        for (i in 0 until bus_stops.length()) {
                            val bus_stop = bus_stops.getJSONObject(i)
                            val busStopValues = ContentValues()
                            busStopValues.put(AreYengContract.BusStopEntry.COLUMN_ID, bus_stop.getString("id"))
                            busStopValues.put(AreYengContract.BusStopEntry.COLUMN_HREF, bus_stop.getString("href"))
                            busStopValues.put(AreYengContract.BusStopEntry.COLUMN_NAME, bus_stop.getString("name"))
                            busStopValues.put(AreYengContract.BusStopEntry.COLUMN_CODE, bus_stop.getString("code"))
                            val geometry = bus_stop.getJSONObject("geometry")
                            busStopValues.put(AreYengContract.BusStopEntry.COLUMN_GEOMETRY_TYPE, geometry.getString("type"))
                            val coordinates = geometry.getJSONArray("coordinates")
                            val bus_modes = bus_stop.getJSONArray("modes")
                            busStopValues.put(AreYengContract.BusStopEntry.COLUMN_GEOMETRY_LONGITUDE, coordinates[0].toString())
                            busStopValues.put(AreYengContract.BusStopEntry.COLUMN_GEOMETRY_LATITUDE, coordinates[1].toString())
                            busStopValues.put(AreYengContract.BusStopEntry.COLUMN_MODES, bus_modes[0].toString())
                            cVVector.add(busStopValues)
                        }
                        // add to database
                        if (cVVector.size > 0) {
                            val cvArray = arrayOfNulls<ContentValues>(cVVector.size)
                            cVVector.toArray(cvArray)
                            contentResolver.bulkInsert(AreYengContract.BusStopEntry.CONTENT_URI, cvArray)
                            updateBusWidget()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.i("Ygritte", "Error : " + t.message)
                }
            })
        }

    fun updateUserDetails() {
        val profileImageView = navigationView!!.getHeaderView(0).findViewById<View>(id.avatar) as ImageView
        val drawer_username = navigationView!!.getHeaderView(0).findViewById<View>(id.name) as TextView
        val drawer_email = navigationView!!.getHeaderView(0).findViewById<View>(id.email) as TextView
        val image_url = mSharedPref!!.getString(Constants.key_user_avatar, "")
        val user_email = mSharedPref!!.getString(Constants.key_sign_up_email, "")
        drawer_email.text = user_email
        if ("" != image_url) {
            Glide.with(this).asBitmap()
                    .load(image_url)
                    .transform(CircleTransform(context))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(object : CustomTarget<Bitmap?>() {
                        override fun onResourceReady(bitmap: Bitmap, anim: Transition<in Bitmap>?) {
                            Palette.generateAsync(bitmap) { palette ->
                                // Here's your generated palette
                                val bgColor = palette!!.getMutedColor(context!!.resources.getColor(android.R.color.darker_gray))
                                //navigationView.setBackgroundResource(bgColor);
                            }
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {}
                    })
        }
    }

    override fun onBackPressed() {
        val drawer = findViewById<View>(id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId
        if (id == R.id.nav_fares) {
            val intent = Intent(this@MainActivity, BusFareActivity::class.java)
            intent.putExtra(Constants.agency_key, agency_intent)
            startActivity(intent)
            /*} else if (id == R.id.nav_journey) {
            Intent intent = new Intent(MainActivity.this, JourneyPlannerActivity.class);
            intent.putExtra(agency_key, agency_intent);
            startActivity(intent); */
        } else if (id == R.id.nav_bus_timetable) {
            val intent = Intent(this@MainActivity, BusTimetableActivity::class.java)
            intent.putExtra(Constants.agency_key, agency_intent)
            startActivity(intent)
        } else if (id == R.id.nav_map) {
            val intent = Intent(this@MainActivity, AReYengMapActivity::class.java)
            intent.putExtra(Constants.agency_key, agency_intent)
            startActivity(intent)
        }
        /*else if (id == R.id.nav_cards) {
            Intent intent = new Intent(MainActivity.this, AReYengCardsActivity.class);
            intent.putExtra(agency_key, agency_intent);
            startActivity(intent);
        }
        */
        val drawer = findViewById<View>(id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onConnected(bundle: Bundle?) {}
    override fun onConnectionSuspended(i: Int) {}
    public override fun onResume() {
        super.onResume()
        supportLoaderManager.restartLoader(FAVOURITE_LOADER, null, this)
        supportLoaderManager.restartLoader(AGENCY_LOADER, null, this)
    }

    public override fun onStop() {
        super.onStop()
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor?> {
        var cursor: CursorLoader? = null
        if (id == FAVOURITE_LOADER) {
            val favourite_uri = AreYengContract.FavoritesBusEntry.CONTENT_URI
            val FAVOURITE_PROJECTION = arrayOf(
                    AreYengContract.FavoritesBusEntry.COLUMN_ID,
                    AreYengContract.FavoritesBusEntry.COLUMN_NAME,
                    AreYengContract.FavoritesBusEntry.COLUMN_DATE_CREATED
            )
            cursor = CursorLoader(this, favourite_uri, FAVOURITE_PROJECTION, null, null, null)
        } else if (id == AGENCY_LOADER) {
            val agency_uri = AreYengContract.AgencyEntry.CONTENT_URI
            val AGENCY_PROJECTION = arrayOf(
                    AreYengContract.AgencyEntry.COLUMN_ID,
                    AreYengContract.AgencyEntry.COLUMN_NAME,
                    AreYengContract.AgencyEntry.COLUMN_CULTURE,
                    AreYengContract.AgencyEntry.COLUMN_HREF
            )
            cursor = CursorLoader(this, agency_uri, AGENCY_PROJECTION, null, null, null)
        }
        return cursor!!
    }

    override fun onLoadFinished(loader: Loader<Cursor?>, data: Cursor?) {
        if (loader.id == FAVOURITE_LOADER) {
            if (data != null && data.moveToFirst()) {
                //Log.i("Ygritte", DatabaseUtils.dumpCursorToString(data));
                favouritesBusStopRecyclerViewAdapter = FavouritesBusStopRecyclerViewAdapter(context!!, data)
                favouritesBusStopRecyclerViewAdapter!!.notifyDataSetChanged()
                busStopFavourites!!.adapter = favouritesBusStopRecyclerViewAdapter
            }
        } else if (loader.id == AGENCY_LOADER) {
            if (data != null && data.moveToFirst()) {
                agency_intent = Agency(data.getString(COL_ID), data.getString(COL_NAME), data.getString(COL_CULTURE), data.getString(COL_HREF))
            }
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor?>) {}
    private fun updateBusWidget() {
        val appWidgetManager = AppWidgetManager.getInstance(context!!.applicationContext)
        val ids = appWidgetManager.getAppWidgetIds(ComponentName(this, BusStopWidgetService::class.java))
        if (ids.size > 0) {
            appWidgetManager.notifyAppWidgetViewDataChanged(ids, id.bus_stop_widget_layout)
        }
    }

    companion object {
        private const val FAVOURITE_LOADER = 0
        private const val AGENCY_LOADER = 1
        private const val COL_ID = 0
        private const val COL_NAME = 1
        private const val COL_CULTURE = 2
        private const val COL_HREF = 3
    }
}
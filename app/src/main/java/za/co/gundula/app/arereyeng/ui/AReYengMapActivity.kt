package za.co.gundula.app.arereyeng.ui

import android.Manifest
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import za.co.gundula.app.arereyeng.R
import za.co.gundula.app.arereyeng.data.AreYengContract
import za.co.gundula.app.arereyeng.model.Agency
import za.co.gundula.app.arereyeng.ui.AReYengMapActivity
import za.co.gundula.app.arereyeng.utils.Constants

class AReYengMapActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor?>, OnMapReadyCallback {

    @BindView(R.id.toolbar)
    var toolbar: Toolbar? = null
    private var mMap: GoogleMap? = null
    var agency: Agency? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_are_yeng_map)
        ButterKnife.bind(this)
        val bundle = intent.extras
        if (bundle != null) {
            agency = bundle.getParcelable(Constants.agency_key)
        }
        toolbar!!.title = resources.getString(R.string.areyeng_map)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        supportLoaderManager.initLoader(BUS_STOP_LOADER, null, this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this@AReYengMapActivity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                val builder = AlertDialog.Builder(this)
                builder.setMessage(getString(R.string.location_permissions))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.dialog_ok)) { dialog, id -> ActivityCompat.requestPermissions(this@AReYengMapActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), Constants.REQUEST_LOCATION) }
                val alert = builder.create()
                alert.show()
            } else {
                ActivityCompat.requestPermissions(this@AReYengMapActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), Constants.REQUEST_LOCATION)
            }
        } else {
            mMap!!.isMyLocationEnabled = true
            mMap!!.uiSettings.isMyLocationButtonEnabled = true
        }
        mMap!!.mapType = GoogleMap.MAP_TYPE_TERRAIN
        mMap!!.isBuildingsEnabled = true
        mMap!!.isIndoorEnabled = false
        mMap!!.uiSettings.isZoomControlsEnabled = true
        mMap!!.uiSettings.isCompassEnabled = true
        mMap!!.animateCamera(CameraUpdateFactory.zoomIn())
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor?> {
        val bus_stop_uri = AreYengContract.BusStopEntry.CONTENT_URI
        val BUS_STOP_PROJECTION = arrayOf(
                AreYengContract.BusStopEntry.COLUMN_ID,
                AreYengContract.BusStopEntry.COLUMN_NAME,
                AreYengContract.BusStopEntry.COLUMN_HREF,
                AreYengContract.BusStopEntry.COLUMN_GEOMETRY_LATITUDE,
                AreYengContract.BusStopEntry.COLUMN_GEOMETRY_LONGITUDE,
                AreYengContract.BusStopEntry.COLUMN_GEOMETRY_TYPE)
        return CursorLoader(
                this,
                bus_stop_uri,
                BUS_STOP_PROJECTION,
                null,
                null,
                null
        )
    }

    public override fun onResume() {
        super.onResume()
        supportLoaderManager.restartLoader(BUS_STOP_LOADER, null, this)
    }

    override fun onLoadFinished(loader: Loader<Cursor?>, data: Cursor?) {
        if (data != null && data.moveToFirst()) {
            do {
                val station_name = data.getString(1)
                val station_url = data.getString(2)
                val station_latitude = data.getDouble(3)
                val station_longitude = data.getDouble(4)
                addMapMarker(station_latitude, station_longitude, station_name, station_name, 10)
            } while (data.moveToNext())
        }
    }

    override fun onLoaderReset(loader: Loader<*>?) {}
    fun addMapMarker(latitude: Double, longitude: Double, title: String?, snippet: String?, iconResId: Int) {
        mMap!!.addMarker(MarkerOptions()
                .position(LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .title(title)
                .snippet(snippet))
    }

    companion object {
        private const val BUS_STOP_LOADER = 0
    }
}
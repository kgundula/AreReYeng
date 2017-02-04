package za.co.gundula.app.arereyeng.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import za.co.gundula.app.arereyeng.R;
import za.co.gundula.app.arereyeng.data.AreYengContract;
import za.co.gundula.app.arereyeng.model.Agency;

public class AReYengMapActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, OnMapReadyCallback {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private GoogleMap mMap;

    Agency agency = null;
    private static final int BUS_STOP_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_are_yeng_map);

        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            agency = bundle.getParcelable("agency_key");
        }


        toolbar.setTitle(getResources().getString(R.string.areyeng_map));
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getSupportLoaderManager().initLoader(BUS_STOP_LOADER, null, this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        mMap.setBuildingsEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
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
            //Log.i("Ygritte", DatabaseUtils.dumpCursorToString(data));
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}

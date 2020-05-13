package za.co.gundula.app.arereyeng.ui;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Vector;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import za.co.gundula.app.arereyeng.R;
import za.co.gundula.app.arereyeng.adapter.FavouritesBusStopRecyclerViewAdapter;
import za.co.gundula.app.arereyeng.data.AreYengContract;
import za.co.gundula.app.arereyeng.model.Agency;
import za.co.gundula.app.arereyeng.rest.WhereIsMyTransportApiClient;
import za.co.gundula.app.arereyeng.rest.WhereIsMyTransportApiClientInterface;
import za.co.gundula.app.arereyeng.sync.AreYengSyncAdapter;
import za.co.gundula.app.arereyeng.utils.CircleTransform;
import za.co.gundula.app.arereyeng.utils.Constants;
import za.co.gundula.app.arereyeng.utils.RecylerViewDividerItemDecoration;
import za.co.gundula.app.arereyeng.utils.Utility;

import static za.co.gundula.app.arereyeng.R.id.drawer_layout;
import static za.co.gundula.app.arereyeng.utils.Constants.agency_key;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    @BindView(R.id.content_main)
    CoordinatorLayout content_main;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.info_text)
    TextView info_text;

    @BindView(R.id.bus_stop_favourites)
    RecyclerView bus_stop_favourites;

    SharedPreferences mSharedPref;
    SharedPreferences.Editor mSharedPrefEditor;

    @BindInt(R.integer.num_columns)
    int columns;

    Context context;
    boolean is_agency_already_saved = false;

    private static final int FAVOURITE_LOADER = 0;
    private static final int AGENCY_LOADER = 1;

    private static final int COL_ID = 0;
    private static final int COL_NAME = 1;
    private static final int COL_CULTURE = 2;
    private static final int COL_HREF = 3;

    GridLayoutManager gridLayoutManager;
    FavouritesBusStopRecyclerViewAdapter favouritesBusStopRecyclerViewAdapter;

    WhereIsMyTransportApiClientInterface whereIsMyTransportApiClient;
    Agency[] agency;
    Agency agency_intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        ButterKnife.bind(this);
        toolbar.setSubtitle(R.string.unofficial);
        setSupportActionBar(toolbar);
        mSharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        mSharedPrefEditor = mSharedPref.edit();

        info_text.setText(R.string.favourites);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        AreYengSyncAdapter.initializeSyncAdapter(this);
        updateUserDetails();
        whereIsMyTransportApiClient = WhereIsMyTransportApiClient.getClient(context).create(WhereIsMyTransportApiClientInterface.class);
        is_agency_already_saved = mSharedPref.getBoolean(Constants.agency_already_saved, false);
        if (!is_agency_already_saved) {
            //Agency doesn't change so we only fetch once.
            getAgency();
        } else {
            getSupportLoaderManager().initLoader(AGENCY_LOADER, null, this);
        }

        getSupportLoaderManager().initLoader(FAVOURITE_LOADER, null, this);

        NetworkInfo networkInfo = Utility.getNetworkWorkInfo(context);
        if (networkInfo != null && !networkInfo.isAvailable()) {
            showSnackBar(getString(R.string.no_network_error));
        } else {
            getBuses();
        }

        int columnCount = columns;

        gridLayoutManager = new GridLayoutManager(context, columnCount);
        bus_stop_favourites.setLayoutManager(gridLayoutManager);
        bus_stop_favourites.addItemDecoration(new RecylerViewDividerItemDecoration(context));
        bus_stop_favourites.setHasFixedSize(true);

    }

    public void showSnackBar(String message) {
        Snackbar.make(content_main, message, Snackbar.LENGTH_LONG).show();
    }

    public void getAgency() {

        Call<ResponseBody> call = whereIsMyTransportApiClient.getAgency();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    //
                    String json_response = response.body().string();
                    // The API returns an array instead of a json object hence we make an agenct array
                    agency = new Gson().fromJson(json_response, Agency[].class);
                    //agency_intent = agency[0];
                    ContentValues agencyValues = new ContentValues();
                    agencyValues.put(AreYengContract.AgencyEntry.COLUMN_ID, agency[0].getId());
                    agencyValues.put(AreYengContract.AgencyEntry.COLUMN_NAME, agency[0].getName());
                    agencyValues.put(AreYengContract.AgencyEntry.COLUMN_HREF, agency[0].getHref());
                    agencyValues.put(AreYengContract.AgencyEntry.COLUMN_CULTURE, agency[0].getCulture());

                    //Save Agency to database;
                    try {
                        getContentResolver().insert(AreYengContract.AgencyEntry.CONTENT_URI, agencyValues);
                        getContentResolver().notifyChange(AreYengContract.AgencyEntry.CONTENT_URI, null);
                        mSharedPrefEditor.putBoolean(Constants.agency_already_saved, true).apply();
                        mSharedPrefEditor.putString(Constants.agency_id, agency[0].getId()).apply();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

    }

    public void getBuses() {

        Call<ResponseBody> call = whereIsMyTransportApiClient.getAllBusStops();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {

                    String json_response = response.body().string();
                    //Log.i("Ygritte", json_response);
                    /*
                    * The API returns an array instead of a json object hence we make an agenct array
                    * agency = new Gson().fromJson(json_response, Agency[].class);
                    */

                    JSONArray bus_stops = new JSONArray(json_response);
                    Vector<ContentValues> cVVector = new Vector<ContentValues>(bus_stops.length());

                    for (int i = 0; i < bus_stops.length(); i++) {
                        JSONObject bus_stop = bus_stops.getJSONObject(i);

                        ContentValues busStopValues = new ContentValues();
                        busStopValues.put(AreYengContract.BusStopEntry.COLUMN_ID, bus_stop.getString("id"));
                        busStopValues.put(AreYengContract.BusStopEntry.COLUMN_HREF, bus_stop.getString("href"));
                        busStopValues.put(AreYengContract.BusStopEntry.COLUMN_NAME, bus_stop.getString("name"));
                        busStopValues.put(AreYengContract.BusStopEntry.COLUMN_CODE, bus_stop.getString("code"));
                        JSONObject geometry = bus_stop.getJSONObject("geometry");
                        busStopValues.put(AreYengContract.BusStopEntry.COLUMN_GEOMETRY_TYPE, geometry.getString("type"));
                        JSONArray coordinates = geometry.getJSONArray("coordinates");
                        JSONArray bus_modes = bus_stop.getJSONArray("modes");
                        busStopValues.put(AreYengContract.BusStopEntry.COLUMN_GEOMETRY_LONGITUDE, String.valueOf(coordinates.get(0)));
                        busStopValues.put(AreYengContract.BusStopEntry.COLUMN_GEOMETRY_LATITUDE, String.valueOf(coordinates.get(1)));
                        busStopValues.put(AreYengContract.BusStopEntry.COLUMN_MODES, String.valueOf(bus_modes.get(0)));
                        cVVector.add(busStopValues);

                    }
                    // add to database
                    if (cVVector.size() > 0) {
                        ContentValues[] cvArray = new ContentValues[cVVector.size()];
                        cVVector.toArray(cvArray);
                        getContentResolver().bulkInsert(AreYengContract.BusStopEntry.CONTENT_URI, cvArray);

                        updateBusWidget();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i("Ygritte", "Error : " + t.getMessage());
            }
        });
    }

    public void updateUserDetails() {

        ImageView profileImageView = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.avatar);
        TextView drawer_username = (TextView) navigationView.getHeaderView(0).findViewById(R.id.name);
        TextView drawer_email = (TextView) navigationView.getHeaderView(0).findViewById(R.id.email);
        String image_url = mSharedPref.getString(Constants.key_user_avatar, "");
        String user_email = mSharedPref.getString(Constants.key_sign_up_email, "");
        drawer_email.setText(user_email);

        if (!"".equals(image_url)) {
            Glide.with(this).asBitmap()
                    .load(image_url)
                    .transform(new CircleTransform(context))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> anim) {
                            Palette.generateAsync(bitmap, new Palette.PaletteAsyncListener() {
                                @Override
                                public void onGenerated(Palette palette) {
                                    // Here's your generated palette
                                    int bgColor = palette.getMutedColor(context.getResources().getColor(android.R.color.darker_gray));
                                    //navigationView.setBackgroundResource(bgColor);
                                }
                            });
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    });
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_fares) {
            Intent intent = new Intent(MainActivity.this, BusFareActivity.class);
            intent.putExtra(agency_key, agency_intent);
            startActivity(intent);
        /*} else if (id == R.id.nav_journey) {
            Intent intent = new Intent(MainActivity.this, JourneyPlannerActivity.class);
            intent.putExtra(agency_key, agency_intent);
            startActivity(intent); */
        } else if (id == R.id.nav_bus_timetable) {
            Intent intent = new Intent(MainActivity.this, BusTimetableActivity.class);
            intent.putExtra(agency_key, agency_intent);
            startActivity(intent);
        } else if (id == R.id.nav_map) {
            Intent intent = new Intent(MainActivity.this, AReYengMapActivity.class);
            intent.putExtra(agency_key, agency_intent);
            startActivity(intent);
        }
        /*else if (id == R.id.nav_cards) {
            Intent intent = new Intent(MainActivity.this, AReYengCardsActivity.class);
            intent.putExtra(agency_key, agency_intent);
            startActivity(intent);
        }
        */

        DrawerLayout drawer = (DrawerLayout) findViewById(drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(FAVOURITE_LOADER, null, this);
        getSupportLoaderManager().restartLoader(AGENCY_LOADER, null, this);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        CursorLoader cursor = null;
        if (id == FAVOURITE_LOADER) {
            Uri favourite_uri = AreYengContract.FavoritesBusEntry.CONTENT_URI;

            String[] FAVOURITE_PROJECTION = new String[]{
                    AreYengContract.FavoritesBusEntry.COLUMN_ID,
                    AreYengContract.FavoritesBusEntry.COLUMN_NAME,
                    AreYengContract.FavoritesBusEntry.COLUMN_DATE_CREATED
            };

            cursor = new CursorLoader(this, favourite_uri, FAVOURITE_PROJECTION, null, null, null);
        } else if (id == AGENCY_LOADER) {
            Uri agency_uri = AreYengContract.AgencyEntry.CONTENT_URI;

            String[] AGENCY_PROJECTION = new String[]{
                    AreYengContract.AgencyEntry.COLUMN_ID,
                    AreYengContract.AgencyEntry.COLUMN_NAME,
                    AreYengContract.AgencyEntry.COLUMN_CULTURE,
                    AreYengContract.AgencyEntry.COLUMN_HREF
            };

            cursor = new CursorLoader(this, agency_uri, AGENCY_PROJECTION, null, null, null);
        }

        return cursor;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (loader.getId() == FAVOURITE_LOADER) {

            if (data != null && data.moveToFirst()) {
                //Log.i("Ygritte", DatabaseUtils.dumpCursorToString(data));
                favouritesBusStopRecyclerViewAdapter = new FavouritesBusStopRecyclerViewAdapter(context, data);
                favouritesBusStopRecyclerViewAdapter.notifyDataSetChanged();
                bus_stop_favourites.setAdapter(favouritesBusStopRecyclerViewAdapter);

            }

        } else if (loader.getId() == AGENCY_LOADER) {

            if (data != null && data.moveToFirst()) {
                agency_intent = new Agency(data.getString(COL_ID), data.getString(COL_NAME), data.getString(COL_CULTURE), data.getString(COL_HREF));
            }
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void updateBusWidget() {

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context.getApplicationContext());
        int[] ids = appWidgetManager.getAppWidgetIds(new ComponentName(this, BusStopWidgetService.class));
        if (ids.length > 0) {
            appWidgetManager.notifyAppWidgetViewDataChanged(ids, R.id.bus_stop_widget_layout);
        }
    }

}




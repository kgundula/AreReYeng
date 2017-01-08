package za.co.gundula.app.arereyeng.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import za.co.gundula.app.arereyeng.Constants;
import za.co.gundula.app.arereyeng.R;
import za.co.gundula.app.arereyeng.model.Agency;
import za.co.gundula.app.arereyeng.rest.WhereIsMyTransportApiClient;
import za.co.gundula.app.arereyeng.rest.WhereIsMyTransportApiClientInterface;
import za.co.gundula.app.arereyeng.sync.AreYengSyncAdapter;
import za.co.gundula.app.arereyeng.utils.CircleTransform;

import static za.co.gundula.app.arereyeng.Constants.agency_key;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    SharedPreferences mSharedPref;
    Context context;

    WhereIsMyTransportApiClientInterface whereIsMyTransportApiClient;
    Agency[] agency;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        mSharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        AreYengSyncAdapter.initializeSyncAdapter(this);
        updateUserDetails();
        whereIsMyTransportApiClient = WhereIsMyTransportApiClient.getClient(context).create(WhereIsMyTransportApiClientInterface.class);

        getAgency();

    }

    public void getAgency() {

        Call<ResponseBody> call = whereIsMyTransportApiClient.getAgency();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    //[{"id":"A1JHSPIg_kWV5XRHIepCLw","href":"https://platform.whereismytransport.com/api/agencies/A1JHSPIg_kWV5XRHIepCLw","name":"A Re Yeng","culture":"en","alerts":[]}]
                    String json_response = response.body().string();
                    // The API returns an array instead of a json object hence we make an agenct array
                    agency = new Gson().fromJson(json_response, Agency[].class);
                    Log.d("Ygritte", " Response :  " + agency[0].getId());
                    Log.d("Ygritte", " Response :  " + agency[0].getName());
                    Log.d("Ygritte", " Response :  " + agency[0].getCulture());
                    Log.d("Ygritte", " Response :  " + agency[0].getHref());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

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

        if (image_url != null) {
            Glide.with(context).load(image_url)
                    .asBitmap()
                    .transform(new CircleTransform(context))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(new BitmapImageViewTarget(profileImageView) {
                        @Override
                        public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                            super.onResourceReady(bitmap, anim);
                            Palette.generateAsync(bitmap, new Palette.PaletteAsyncListener() {
                                @Override
                                public void onGenerated(Palette palette) {
                                    // Here's your generated palette
                                    int bgColor = palette.getMutedColor(context.getResources().getColor(android.R.color.darker_gray));
                                    //navigationView.setBackgroundResource(bgColor);
                                }
                            });
                        }
                    });
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
            startActivity(new Intent(MainActivity.this, BusFareActivity.class));
        } else if (id == R.id.nav_journey) {
            startActivity(new Intent(MainActivity.this, JourneyPlannerActivity.class));
        } else if (id == R.id.nav_bus_timetable) {
            Intent intent = new Intent(MainActivity.this, BusTimetableActivity.class);
            intent.putExtra(agency_key, agency[0]);
            startActivity(intent);
        } else if (id == R.id.nav_map) {
            startActivity(new Intent(MainActivity.this, AReYengMapActivity.class));
        } else if (id == R.id.nav_cards) {
            startActivity(new Intent(MainActivity.this, AReYengMapActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}

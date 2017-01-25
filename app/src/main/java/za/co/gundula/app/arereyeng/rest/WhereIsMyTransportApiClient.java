package za.co.gundula.app.arereyeng.rest;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import za.co.gundula.app.arereyeng.BuildConfig;
import za.co.gundula.app.arereyeng.Constants;

/**
 * Created by kgundula on 2016/11/04.
 */

public class WhereIsMyTransportApiClient {

    private static final String BASE_URL = BuildConfig.AreYengApiEndPoint;

    private static Retrofit retrofit = null;

    public static Retrofit getClient(final Context context) {
        if (retrofit == null) {

            Log.i("Ygritte", BASE_URL);
            SharedPreferences mSharedPref = PreferenceManager.getDefaultSharedPreferences(context);

            String token = mSharedPref.getString(Constants.access_token, "");
            String bearer = mSharedPref.getString(Constants.token_type, "");

            RetrofitInterceptor retrofitInterceptor = new RetrofitInterceptor(token, bearer);

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addNetworkInterceptor(retrofitInterceptor);
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();

        }
        return retrofit;
    }

}

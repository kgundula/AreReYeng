package za.co.gundula.app.arereyeng.rest;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import za.co.gundula.app.arereyeng.BuildConfig;

/**
 * Created by kgundula on 2016/11/04.
 */

public class WhereIsMyTransportApiClient {

    private static final String BASE_URL = BuildConfig.AreYengApiEndPoint;

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}

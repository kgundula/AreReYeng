package za.co.gundula.app.arereyeng.rest;

import android.content.Context;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import za.co.gundula.app.arereyeng.BuildConfig;

/**
 * Created by kgundula on 2016/11/04.
 */

public class WhereIsMyTransportApiClient {

    private static final String BASE_URL = BuildConfig.AreYengApiEndPoint;

    private static Retrofit retrofit = null;

    public static Retrofit getClient(final Context context) {
        if (retrofit == null) {


            RetrofitInterceptor retrofitInterceptor = new RetrofitInterceptor(context);
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();

            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addNetworkInterceptor(retrofitInterceptor);
            //httpClient.interceptors().add(logging);
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();


        }
        return retrofit;
    }

}

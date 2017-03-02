package za.co.gundula.app.arereyeng.rest;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import za.co.gundula.app.arereyeng.utils.Constants;

/**
 * Created by kgundula on 2016/12/24.
 */

public class RetrofitInterceptor implements Interceptor {

    String token = "";
    String bearer = "";

    public RetrofitInterceptor(Context context) {
        SharedPreferences mSharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        this.token = mSharedPref.getString(Constants.access_token, "");
        this.bearer = mSharedPref.getString(Constants.token_type, "");

    }

    public RetrofitInterceptor() {
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        // Authorization: Bearer eyJ0eXAiOiJ32aQiLCJhbGciOiJSUzI1NiIsIfg1iCI6ImEzck1VZ01Gd8d0UGNsTGE2eUYz...
        Request.Builder builder = chain.request().newBuilder();
        builder.addHeader("Authorization", bearer + " " + token);
        builder.addHeader("Accept", "application/json");
        return chain.proceed(builder.build());
    }
}

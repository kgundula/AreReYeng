package za.co.gundula.app.arereyeng.rest;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import za.co.gundula.app.arereyeng.BuildConfig;
import za.co.gundula.app.arereyeng.model.ApiToken;

/**
 * Created by kgundula on 2016/11/03.
 */

public class WhereIsMyTransportTokenApiClient {


    private static OkHttpClient clientToken = new OkHttpClient();

    private static final String url = BuildConfig.AreYengApiTokenEndPoint;
    private static final String client_id = BuildConfig.AreYengClientKey;
    private static final String client_secret = BuildConfig.AreYengSecretKey;
    private static final String client_credentials = "client_credentials";
    private static final String scope = "transportapi:all";


    private static final String client_id_name = "client_id";
    private static final String client_secret_name = "client_secret";
    private static final String client_grant_type = "grant_type";
    private static final String client_scope = "grant_type";

    public void getToken() {

        RequestBody body = new FormBody.Builder()
                .add("Content-Type", "application/x-www-form-urlencoded")
                .add(client_id_name, client_id)
                .add(client_secret_name, client_secret)
                .add(client_grant_type, client_credentials)
                .add(client_scope, scope)
                .build();


        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        clientToken.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String responseString = response.body().string();

                try {
                    JSONObject tokenJson = new JSONObject(responseString);

                    Log.i("Ygritte", tokenJson.toString());
                    ApiToken apiToken = new ApiToken(tokenJson.getString("access_token"), tokenJson.getString("expires_in"), tokenJson.getString("token_type"));
                    Log.i("Ygritte : token ", apiToken.getAccess_token());
                    Log.i("Ygritte : expires", apiToken.getExpires_in());
                    Log.i("Ygritte : toke type", apiToken.getToken_type());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }


}

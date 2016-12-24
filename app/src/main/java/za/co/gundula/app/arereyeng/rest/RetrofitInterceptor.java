package za.co.gundula.app.arereyeng.rest;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by kgundula on 2016/12/24.
 */

public class RetrofitInterceptor implements Interceptor {


    String token = "";
    String bearer = "";

    public RetrofitInterceptor(String token, String bearer) {
        this.token = token;
        this.bearer = bearer;
    }

    public RetrofitInterceptor() {
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        // Authorization: Bearer eyJ0eXAiOiJ32aQiLCJhbGciOiJSUzI1NiIsIfg1iCI6ImEzck1VZ01Gd8d0UGNsTGE2eUYz...
        Request.Builder builder = chain.request().newBuilder();
        builder.addHeader("Authorization", bearer + " " + token);

        return chain.proceed(builder.build());
    }
}

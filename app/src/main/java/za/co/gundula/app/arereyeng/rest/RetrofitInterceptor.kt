package za.co.gundula.app.arereyeng.rest

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import okhttp3.Interceptor
import okhttp3.Response
import za.co.gundula.app.arereyeng.utils.Constants
import java.io.IOException

/**
 * Created by kgundula on 2016/12/24.
 */
class RetrofitInterceptor : Interceptor {
    var mSharedPref: SharedPreferences? = null

    constructor(context: Context?) {
        mSharedPref = PreferenceManager.getDefaultSharedPreferences(context)
    }

    constructor() {}

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = mSharedPref!!.getString(Constants.access_token, "")
        val bearer = mSharedPref!!.getString(Constants.token_type, "")

        // Authorization: Bearer eyJ0eXAiOiJ32aQiLCJhbGciOiJSUzI1NiIsIfg1iCI6ImEzck1VZ01Gd8d0UGNsTGE2eUYz...
        val builder = chain.request().newBuilder()
        builder.addHeader("Authorization", "$bearer $token")
        builder.addHeader("Accept", "application/json")
        return chain.proceed(builder.build())
    }
}
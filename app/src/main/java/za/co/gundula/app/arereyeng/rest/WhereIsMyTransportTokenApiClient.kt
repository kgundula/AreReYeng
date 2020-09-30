package za.co.gundula.app.arereyeng.rest

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import za.co.gundula.app.arereyeng.BuildConfig
import za.co.gundula.app.arereyeng.model.ApiToken
import za.co.gundula.app.arereyeng.utils.Constants
import java.io.IOException

/**
 * Created by kgundula on 2016/11/03.
 */
class WhereIsMyTransportTokenApiClient {

    private var mSharedPref: SharedPreferences? = null
    private var mSharedPrefEditor: SharedPreferences.Editor? = null
    fun getToken(context: Context?) {
        val body: RequestBody = FormBody.Builder()
                .add("Content-Type", "application/x-www-form-urlencoded")
                .add(client_id_name, client_id)
                .add(client_secret_name, client_secret)
                .add(client_grant_type, client_credentials)
                .add(client_scope, scope)
                .build()
        val request = Request.Builder()
                .url(url)
                .post(body)
                .build()
        clientToken.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val responseString = response.body!!.string()
                try {
                    val tokenJson = JSONObject(responseString)
                    val apiToken = ApiToken(tokenJson.getString("access_token"), tokenJson.getString("expires_in"), tokenJson.getString("token_type"))
                    mSharedPref = PreferenceManager.getDefaultSharedPreferences(context)
                    mSharedPrefEditor = mSharedPref.edit()
                    // Save Authorisation tokens on shared preferences for faster access
                    mSharedPrefEditor.putString(Constants.token_type, apiToken.tokenType).apply()
                    mSharedPrefEditor.putString(Constants.access_token, apiToken.accessToken).apply()
                    mSharedPrefEditor.putString(Constants.expires_in, apiToken.expiresIn).apply()
                    Log.i("Ygritte", "Token : " + apiToken.accessToken)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        })
    }

    companion object {
        private val clientToken = OkHttpClient()
        private val url: String = BuildConfig.AreYengApiTokenEndPoint
        private val client_id: String = BuildConfig.AreYengClientKey
        private val client_secret: String = BuildConfig.AreYengSecretKey
        private const val client_credentials = "client_credentials"
        private const val scope = "transportapi:all"
        private const val client_id_name = "client_id"
        private const val client_secret_name = "client_secret"
        private const val client_grant_type = "grant_type"
        private const val client_scope = "grant_type"
    }
}
package za.co.gundula.app.arereyeng.rest

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import za.co.gundula.app.arereyeng.BuildConfig

/**
 * Created by kgundula on 2016/11/04.
 */
object WhereIsMyTransportApiClient {
    private val BASE_URL: String = BuildConfig.AreYengApiEndPoint
    private var retrofit: Retrofit? = null
    @JvmStatic
    fun getClient(context: Context?): Retrofit? {
        if (retrofit == null) {
            val retrofitInterceptor = RetrofitInterceptor(context)
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val httpClient = OkHttpClient.Builder()
            httpClient.addNetworkInterceptor(retrofitInterceptor)
            //httpClient.interceptors().add(logging);
            retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build()
        }
        return retrofit
    }
}
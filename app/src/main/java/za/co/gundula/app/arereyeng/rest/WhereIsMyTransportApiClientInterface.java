package za.co.gundula.app.arereyeng.rest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by kgundula on 2016/11/09.
 */

public interface WhereIsMyTransportApiClientInterface {

    @GET("api/agencies")
    Call<ResponseBody> getAgency();

    @GET("api/stops")
    Call<ResponseBody> getAllBusStops();

    @GET("api/stops")
    Call<ResponseBody> getBusStops(@Query("id") String id);

    // api/stops?agencies=5kcfZkKW0ku4Uk-A6j8MFA&point=-33.923,18.421&radius=500
    @GET("api/stops?agencies")
    Call<ResponseBody> getAllBusStops(@Query("id") String id);

    @GET("api/fareproducts?agencies")
    Call<ResponseBody> getFareProducts(@Query("id") String id);

    @GET("api/lines?agencies")
    Call<ResponseBody> getLines(@Query("id") String id);

}

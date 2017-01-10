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
    Call<ResponseBody> getBusStops(@Query("id") String id);

}

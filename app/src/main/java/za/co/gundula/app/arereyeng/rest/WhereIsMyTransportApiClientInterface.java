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
    @GET("api/stops?agencies=")
    Call<ResponseBody> getAllBusStops(@Query("id") String id);

    //api/stops/eBTeYLPXOkWm5zyfjZVaZg/timetables?limit=2
    //@GET()

    //api/lines?agencies=5kcfZkKW0ku4Uk-A6j8MFA&limit=2

    //api/lines/rBD_j-ZRdEiiHMc9lNzQtA

    //api/lines/{id}/timetables?earliestDepartureTime={DateTime}&latestDepartureTime={DateTime}&departureStopId={stop}&arrivalStopId={stop}&limit={int}&offset={int}

    //api/journeys - post a journey

    //api/journeys/8GYKddjcAk6j7aVUAMV3pw/itineraries/dnCQV5Kq0kaq5KVUAMV_eQ

    //api/fareproducts?agencies={Identifiers}&limit={int}&offset={int}
    //api/fareproducts?agencies=5kcfZkKW0ku4Uk-A6j8MFA&limit=2

}

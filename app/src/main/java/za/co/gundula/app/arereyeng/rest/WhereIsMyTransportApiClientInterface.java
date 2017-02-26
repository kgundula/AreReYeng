package za.co.gundula.app.arereyeng.rest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import za.co.gundula.app.arereyeng.model.Journey;

/**
 * Created by kgundula on 2016/11/09.
 */

public interface WhereIsMyTransportApiClientInterface {

    @GET("api/agencies")
    Call<ResponseBody> getAgency();

    @GET("api/stops")
    Call<ResponseBody> getAllBusStops();

    //api/stops/{id}/stops
    @GET("api/stops/{id}/stops")
    Call<ResponseBody> getBusStops(@Path("id") String id);

    // api/stops?agencies=5kcfZkKW0ku4Uk-A6j8MFA&point=-33.923,18.421&radius=500
    //@GET("api/stops?agencies")
    //Call<ResponseBody> getAllBusStops(@Query("id") String id);

    @GET("api/fareproducts")
    Call<ResponseBody> getFareProducts(@Query("agencies") String agencies);

    @GET("api/lines?agencies")
    Call<ResponseBody> getLines(@Query("id") String id);

    //api/lines/rBD_j-ZRdEiiHMc9lNzQtA
    @GET("api/lines/")
    Call<ResponseBody> getLine(@Query("id") String id);

    //api/lines/{id}/timetables?earliestDepartureTime={DateTime}&latestDepartureTime={DateTime}
    // &departureStopId={stop}&arrivalStopId={stop}&limit={int}&offset={int}

    @GET("api/lines/{id}/timetables")
    Call<ResponseBody> getLineTimetable(@Path("id") String id, @Query("earliestDepartureTime") String earliestDepartureTime,
                                        @Query("latestDepartureTime") String latestDepartureTime, @Query("departureStopId") String departureStopId,
                                        @Query("arrivalStopId") String arrivalStopId, @Query("limit") String limit);

    //api/stops/{id}/timetables?earliestArrivalTime={DateTime}&latestArrivalTime={DateTime}limit={int}&offset={int}
    @GET("api/stops/{id}/timetables")
    Call<ResponseBody> getStopTimetable(@Path("id") String id);
    //, @Query("earliestArrivalTime") String earliestArrivalTime,@Query("latestArrivalTime") String latestArrivalTime, @Query("limit") int limit, @Query("offset") int offset );

    @POST("api/journeys")
    Call<ResponseBody> postJourney(@Body Journey journey);
}

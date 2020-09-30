package za.co.gundula.app.arereyeng.rest

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import za.co.gundula.app.arereyeng.model.Journey

/**
 * Created by kgundula on 2016/11/09.
 */
interface WhereIsMyTransportApiClientInterface {
    @get:GET("api/agencies")
    val agency: Call<ResponseBody?>?

    @get:GET("api/stops")
    val allBusStops: Call<ResponseBody?>?

    //api/stops/{id}/stops
    @GET("api/stops/{id}/stops")
    fun getBusStops(@Path("id") id: String?): Call<ResponseBody?>?

    // api/stops?agencies=5kcfZkKW0ku4Uk-A6j8MFA&point=-33.923,18.421&radius=500
    @GET("api/stops?agencies")
    fun getAllBusStops(@Query("id") id: String?): Call<ResponseBody?>?

    @GET("api/fareproducts")
    fun getFareProducts(@Query("agencies") agencies: String?): Call<ResponseBody?>?

    @GET("api/lines?agencies")
    fun getLines(@Query("id") id: String?): Call<ResponseBody?>?

    //api/lines/rBD_j-ZRdEiiHMc9lNzQtA
    /*@GET("api/lines/")
    fun getLine(@Query("id") id: String?): Call<ResponseBody?>?*/

    //api/lines/{id}/timetables?earliestDepartureTime={DateTime}&latestDepartureTime={DateTime}
    // &departureStopId={stop}&arrivalStopId={stop}&limit={int}&offset={int}
    @GET("api/lines/{id}/timetables")
    fun getLineTimetable(@Path("id") id: String?, @Query("earliestDepartureTime") earliestDepartureTime: String?,
                         @Query("latestDepartureTime") latestDepartureTime: String?, @Query("departureStopId") departureStopId: String?,
                         @Query("arrivalStopId") arrivalStopId: String?, @Query("limit") limit: String?): Call<ResponseBody?>?

    //api/stops/{id}/timetables?earliestArrivalTime={DateTime}&latestArrivalTime={DateTime}limit={int}&offset={int}
    @GET("api/stops/{id}/timetables")
    fun getStopTimetable(@Path("id") id: String?): Call<ResponseBody?>?

    //, @Query("earliestArrivalTime") String earliestArrivalTime,@Query("latestArrivalTime") String latestArrivalTime, @Query("limit") int limit, @Query("offset") int offset );
    @POST("api/journeys?exclude=geometry,directions,waypoints,line")
    fun calculateJourneyFare(@Body journey: Journey?): Call<ResponseBody?>?
}
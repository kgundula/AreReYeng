package za.co.gundula.app.arereyeng.rest;

import retrofit2.Call;
import retrofit2.http.GET;
import za.co.gundula.app.arereyeng.model.Agency;

/**
 * Created by kgundula on 2016/11/09.
 */

public interface WhereIsMyTransportApiClientInterface {

    @GET("api/agencies")
    Call<Agency> getAgency();

}

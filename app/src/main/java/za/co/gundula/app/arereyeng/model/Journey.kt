package za.co.gundula.app.arereyeng.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by kgundula on 2017/02/26.
 */
class Journey(@SerializedName("geometry")
              @Expose var geometry: Geometry?, @SerializedName("time")
              @Expose var time: String?, @SerializedName("timeType")
              @Expose var timeType: String?, @SerializedName("fareProducts")
              @Expose var fareProducts: List<String>?) {

}
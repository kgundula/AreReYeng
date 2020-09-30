package za.co.gundula.app.arereyeng.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by kgundula on 2017/01/07.
 */
class Geometry(@SerializedName("type")
               @Expose var type: String?, @SerializedName("coordinates")
               @Expose var coordinates: List<List<Double>>?) {

}
package za.co.gundula.app.arereyeng.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by kgundula on 2017/01/07.
 */

public class Geometry {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("coordinates")
    @Expose
    private List<List<Double>> coordinates = null;

    public Geometry() {
    }

    public Geometry(String type, List<List<Double>> coordinates) {
        this.type = type;
        this.coordinates = coordinates;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<List<Double>> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<List<Double>> coordinates) {
        this.coordinates = coordinates;
    }

}

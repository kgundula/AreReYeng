package za.co.gundula.app.arereyeng.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by kgundula on 2017/02/26.
 */

public class Journey {

    @SerializedName("geometry")
    @Expose
    private Geometry geometry;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("timeType")
    @Expose
    private String timeType;
    @SerializedName("fareProducts")
    @Expose
    private List<String> fareProducts = null;

    public Journey() {
    }

    public Journey(Geometry geometry, String time, String timeType, List<String> fareProducts) {
        this.geometry = geometry;
        this.time = time;
        this.timeType = timeType;
        this.fareProducts = fareProducts;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTimeType() {
        return timeType;
    }

    public void setTimeType(String timeType) {
        this.timeType = timeType;
    }

    public List<String> getFareProducts() {
        return fareProducts;
    }

    public void setFareProducts(List<String> fareProducts) {
        this.fareProducts = fareProducts;
    }


}

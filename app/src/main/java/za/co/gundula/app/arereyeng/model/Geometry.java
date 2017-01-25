package za.co.gundula.app.arereyeng.model;

/**
 * Created by kgundula on 2017/01/07.
 */

public class Geometry {

    private String type;
    private Integer[] coordinates;

    public Geometry(String type, Integer[] coordinates) {
        this.type = type;
        this.coordinates = coordinates;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Integer[] coordinates) {
        this.coordinates = coordinates;
    }
}

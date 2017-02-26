package za.co.gundula.app.arereyeng.model;

/**
 * Created by kgundula on 2017/02/21.
 */

public class BusTimeTable {

    private String arrivalTime;
    private String departureTime;
    protected Vehicle vehicle;
    protected BusLine busLine;

    public BusTimeTable() {
    }

    public BusTimeTable(String arrivalTime, String departureTime, Vehicle vehicle, BusLine busLine) {
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.vehicle = vehicle;
        this.busLine = busLine;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public BusLine getBusLine() {
        return busLine;
    }

    public void setBusLine(BusLine busLine) {
        this.busLine = busLine;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }
}

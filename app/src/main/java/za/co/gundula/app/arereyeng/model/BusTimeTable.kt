package za.co.gundula.app.arereyeng.model

/**
 * Created by kgundula on 2017/02/21.
 */
class BusTimeTable {
    lateinit var busLine: BusLine
    lateinit var arrivalTime: String
    lateinit var departureTime: String
    lateinit var vehicle: Vehicle

    constructor(arrivalTime: String, departureTime: String, vehicle: Vehicle, busLine: BusLine) {
        this.arrivalTime = arrivalTime
        this.departureTime = departureTime
        this.vehicle = vehicle
        this.busLine = busLine
    }

}
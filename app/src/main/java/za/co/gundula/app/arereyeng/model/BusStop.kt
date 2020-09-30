package za.co.gundula.app.arereyeng.model

/**
 * Created by kgundula on 2017/01/07.
 */
/*
    {
        "id": "FGHU1tF_40S5ZOAYimd7zQ",
        "href": "https://platform.whereismytransport.com/api/stops/FGHU1tF_40S5ZOAYimd7zQ",
        "agency": {
            "id": "A1JHSPIg_kWV5XRHIepCLw",
            "href": "https://platform.whereismytransport.com/api/agencies/A1JHSPIg_kWV5XRHIepCLw",
            "name": "A Re Yeng",
            "culture": "en",
            "alerts": []
        },
        "name": "Annie Botha",
        "code": "Annie Botha",
        "geometry": {
            "type": "Point",
            "coordinates": [28.204797, -25.732453]
        },
        "modes": ["Bus"],
        "alerts": []
        }
    */
class BusStop(var id: String, var href: String, var agency_id: String, var name: String, var geometry: Geometry, var modes: Array<String>, var alerts: Array<String>)
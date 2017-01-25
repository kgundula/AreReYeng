package za.co.gundula.app.arereyeng.model;

/**
 * Created by kgundula on 2017/01/07.
 */

public class BusStop {

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
    private String id;
    private String href;
    private String agency_id;
    private String name;
    Geometry geometry;
    private String[] modes;
    private String[] alerts;

    public BusStop(String id, String href, String agency_id, String name, Geometry geometry, String[] modes, String[] alerts) {
        this.id = id;
        this.href = href;
        this.agency_id = agency_id;
        this.name = name;
        this.geometry = geometry;
        this.modes = modes;
        this.alerts = alerts;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getAgency_id() {
        return agency_id;
    }

    public void setAgency_id(String agency_id) {
        this.agency_id = agency_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public String[] getModes() {
        return modes;
    }

    public void setModes(String[] modes) {
        this.modes = modes;
    }

    public String[] getAlerts() {
        return alerts;
    }

    public void setAlerts(String[] alerts) {
        this.alerts = alerts;
    }
}

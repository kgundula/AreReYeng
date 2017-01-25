package za.co.gundula.app.arereyeng.model;

/**
 * Created by kgundula on 2017/01/24.
 */

public class FareProduct {

    private String id;
    private String href;
    private String name;
    private String agency_id;
    private boolean isDefault;

    public FareProduct(String id, String href, String name, String agency_id, boolean isDefault) {
        this.id = id;
        this.href = href;
        this.name = name;
        this.agency_id = agency_id;
        this.isDefault = isDefault;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAgency_id() {
        return agency_id;
    }

    public void setAgency_id(String agency_id) {
        this.agency_id = agency_id;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }
}

package za.co.gundula.app.arereyeng.model;

/**
 * Created by kgundula on 2016/12/24.
 */

public class Agency {

    private String id;
    private String href;
    private String name;
    private String culture;
    private String description;

    public Agency(String id, String href, String name, String culture, String description) {
        this.id = id;
        this.href = href;
        this.name = name;
        this.culture = culture;
        this.description = description;
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

    public String getCulture() {
        return culture;
    }

    public void setCulture(String culture) {
        this.culture = culture;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

package za.co.gundula.app.arereyeng.model;

/**
 * Created by kgundula on 2017/02/21.
 */

public class BusLine {

    private String id;
    private String href;
    protected Agency agency;
    private String name;
    private String shortName;
    private String mode;
    private String colour;
    private String textColour;

    public BusLine() {
    }

    public BusLine(String id, String href, Agency agency, String name, String shortName, String mode, String colour, String textColour) {
        this.id = id;
        this.href = href;
        this.agency = agency;
        this.name = name;
        this.shortName = shortName;
        this.mode = mode;
        this.colour = colour;
        this.textColour = textColour;
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

    public Agency getAgency() {
        return agency;
    }

    public void setAgency(Agency agency) {
        this.agency = agency;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public String getTextColour() {
        return textColour;
    }

    public void setTextColour(String textColour) {
        this.textColour = textColour;
    }
}

package za.co.gundula.app.arereyeng.model;

/**
 * Created by kgundula on 2016/11/09.
 */

public class Fares {

    private String description;
    private FareProduct fareProduct;
    private String cost;
    private String messages;

    public Fares() {
    }

    public Fares(String description, FareProduct fareProduct, String cost, String messages) {
        this.description = description;
        this.fareProduct = fareProduct;
        this.cost = cost;
        this.messages = messages;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public FareProduct getFareProduct() {
        return fareProduct;
    }

    public void setFareProduct(FareProduct fareProduct) {
        this.fareProduct = fareProduct;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getMessages() {
        return messages;
    }

    public void setMessages(String messages) {
        this.messages = messages;
    }
}

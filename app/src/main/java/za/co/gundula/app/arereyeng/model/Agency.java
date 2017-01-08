package za.co.gundula.app.arereyeng.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kgundula on 2016/12/24.
 */

public class Agency implements Parcelable {

    private String id;
    private String href;
    private String name;
    private String culture;

    public Agency(String id, String href, String name, String culture) {
        this.id = id;
        this.href = href;
        this.name = name;
        this.culture = culture;
    }

    protected Agency(Parcel in) {
        id = in.readString();
        href = in.readString();
        name = in.readString();
        culture = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(href);
        dest.writeString(name);
        dest.writeString(culture);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Agency> CREATOR = new Creator<Agency>() {
        @Override
        public Agency createFromParcel(Parcel in) {
            return new Agency(in);
        }

        @Override
        public Agency[] newArray(int size) {
            return new Agency[size];
        }
    };

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


}

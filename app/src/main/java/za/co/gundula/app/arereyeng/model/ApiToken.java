package za.co.gundula.app.arereyeng.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kgundula on 2016/11/09.
 */

public class ApiToken implements Parcelable {

    private String access_token;
    private String expires_in;
    private String token_type;

    public ApiToken() {
    }

    public ApiToken(String access_token, String expires_in, String token_type) {
        this.access_token = access_token;
        this.expires_in = expires_in;
        this.token_type = token_type;
    }

    protected ApiToken(Parcel in) {
        access_token = in.readString();
        expires_in = in.readString();
        token_type = in.readString();
    }

    public static final Creator<ApiToken> CREATOR = new Creator<ApiToken>() {
        @Override
        public ApiToken createFromParcel(Parcel in) {
            return new ApiToken(in);
        }

        @Override
        public ApiToken[] newArray(int size) {
            return new ApiToken[size];
        }
    };

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(String expires_in) {
        this.expires_in = expires_in;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(access_token);
        parcel.writeString(expires_in);
        parcel.writeString(token_type);
    }
}

package za.co.gundula.app.arereyeng.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by kgundula on 2016/11/09.
 */
class ApiToken : Parcelable {
    var accessToken: String? = null
    var expiresIn: String? = null
    var tokenType: String? = null

    constructor(access_token: String?, expires_in: String?, token_type: String?) {
        this.accessToken = access_token
        this.expiresIn = expires_in
        this.tokenType = token_type
    }

    protected constructor(`in`: Parcel) {
        accessToken = `in`.readString()
        expiresIn = `in`.readString()
        tokenType = `in`.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, i: Int) {
        parcel.writeString(accessToken)
        parcel.writeString(expiresIn)
        parcel.writeString(tokenType)
    }

    companion object CREATOR : Parcelable.Creator<ApiToken> {
        override fun createFromParcel(parcel: Parcel): ApiToken {
            return ApiToken(parcel)
        }

        override fun newArray(size: Int): Array<ApiToken?> {
            return arrayOfNulls(size)
        }
    }

}
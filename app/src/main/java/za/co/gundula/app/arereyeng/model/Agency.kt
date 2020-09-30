package za.co.gundula.app.arereyeng.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by kgundula on 2016/12/24.
 */
class Agency() : Parcelable {
    lateinit var id: String
    lateinit var href: String
    lateinit var name: String
    lateinit var culture: String

    constructor(parcel: Parcel) : this() {
        this.id = parcel.readString().toString()
        this.href = parcel.readString().toString()
        this.name = parcel.readString().toString()
        this.culture = parcel.readString().toString()
    }

    constructor(id: String?, href: String?, name: String?, culture: String?) : this() {
        this.id = id!!
        this.href = href!!
        this.name = name!!
        this.culture = culture!!
    }


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(href)
        parcel.writeString(name)
        parcel.writeString(culture)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Agency> {
        override fun createFromParcel(parcel: Parcel): Agency {
            return Agency(parcel)
        }

        override fun newArray(size: Int): Array<Agency?> {
            return arrayOfNulls(size)
        }
    }


}
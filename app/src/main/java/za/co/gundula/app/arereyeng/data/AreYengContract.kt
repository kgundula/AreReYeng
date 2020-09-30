package za.co.gundula.app.arereyeng.data

import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.provider.BaseColumns

/**
 * Created by kgundula on 2016/12/24.
 */
object AreYengContract {
    const val CONTENT_AUTHORITY = "areyeng.gundula.co.za"
    val BASE_CONTENT_URI = Uri.parse("content://$CONTENT_AUTHORITY")
    const val PATH_FARES = "fares"
    const val PATH_JOURNEY = "journey"
    const val PATH_AGENCY = "agency"
    const val PATH_BUS_STOP = "bus_stops"
    const val PATH_FARE_PRODUCT = "fare_product"
    const val PATH_GEOMETRY = "geometry"
    const val PATH_FAVOURITES = "favourites_stop"

    object FaresEntry : BaseColumns {
        val CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FARES).build()
        const val CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FARES
        const val CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FARES

        // Table name
        const val TABLE_NAME = "fares"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_FARE_PRODUCT = "fareProduct"
        const val COLUMN_COST = "cost"
        const val COLUMN_MESSAGES = "messages"
        fun buildFaresUri(_id: Long): Uri {
            return ContentUris.withAppendedId(CONTENT_URI, _id)
        }
    }

    object AgencyEntry : BaseColumns {
        @JvmField
        val CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_AGENCY).build()
        const val CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_AGENCY
        const val CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_AGENCY

        // Table name
        const val TABLE_NAME = "agency"
        const val COLUMN_ID = "id"
        const val COLUMN_HREF = "href"
        const val COLUMN_NAME = "name"
        const val COLUMN_CULTURE = "culture"
        fun buildAgencyUri(_id: Long): Uri {
            return ContentUris.withAppendedId(CONTENT_URI, _id)
        }
    }

    object BusStopEntry : BaseColumns {
        @JvmField
        val CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_BUS_STOP).build()
        const val CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BUS_STOP
        const val CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BUS_STOP

        // Table name
        const val TABLE_NAME = "bus_stops"
        const val COLUMN_ID = "id"
        const val COLUMN_HREF = "href"
        const val COLUMN_NAME = "name"
        const val COLUMN_CODE = "code"
        const val COLUMN_GEOMETRY_TYPE = "geometry_type"
        const val COLUMN_GEOMETRY_LATITUDE = "geometry_latitude"
        const val COLUMN_GEOMETRY_LONGITUDE = "geometry_longitude"
        const val COLUMN_MODES = "modes"
        fun buildBusStopUri(_id: Long): Uri {
            return ContentUris.withAppendedId(CONTENT_URI, _id)
        }
    }

    object GeometryEntry : BaseColumns {
        val CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_GEOMETRY).build()
        const val CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GEOMETRY
        const val CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GEOMETRY

        // Table name
        const val TABLE_NAME = "geometry"
        const val COLUMN_ID = "id"
    }

    object FareProductEntry : BaseColumns {
        @JvmField
        val CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FARE_PRODUCT).build()
        const val CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FARE_PRODUCT
        const val CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FARE_PRODUCT

        // Table name
        const val TABLE_NAME = "fare_product"
        const val COLUMN_ID = "id"
        const val COLUMN_HREF = "href"
        const val COLUMN_NAME = "name"
        const val COLUMN_AGENCY_ID = "agency_id"
        const val COLUMN_IS_DEFAULT = "isDefault"
        fun buildFareProductUri(_id: Long): Uri {
            return ContentUris.withAppendedId(CONTENT_URI, _id)
        }
    }

    object FavoritesBusEntry : BaseColumns {
        @JvmField
        val CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVOURITES).build()
        const val CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVOURITES
        const val CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVOURITES

        // Table name
        const val TABLE_NAME = "favourites_bus_stops"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_DATE_CREATED = "date_created"
        fun buildFavouriteBusStopUri(_id: Long): Uri {
            return ContentUris.withAppendedId(CONTENT_URI, _id)
        }
    }
}
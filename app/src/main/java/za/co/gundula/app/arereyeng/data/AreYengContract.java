package za.co.gundula.app.arereyeng.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by kgundula on 2016/12/24.
 */

public class AreYengContract {

    public static final String CONTENT_AUTHORITY = "areyeng.gundula.co.za";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_FARES = "fares";
    public static final String PATH_JOURNEY = "journey";
    public static final String PATH_AGENCY = "agency";
    public static final String PATH_BUS_STOP = "bus_stops";
    public static final String PATH_FARE_PRODUCT = "fare_product";
    public static final String PATH_GEOMETRY = "geometry";
    public static final String PATH_FAVOURITES = "favourites_stop";

    public static final class FaresEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FARES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FARES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FARES;

        // Table name
        public static final String TABLE_NAME = "fares";

        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_FARE_PRODUCT = "fareProduct";
        public static final String COLUMN_COST = "cost";
        public static final String COLUMN_MESSAGES = "messages";

        public static Uri buildFaresUri(long _id) {
            return ContentUris.withAppendedId(CONTENT_URI, _id);
        }
    }

    public static final class AgencyEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_AGENCY).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_AGENCY;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_AGENCY;

        // Table name
        public static final String TABLE_NAME = "agency";

        public static final String COLUMN_ID = "id";
        public static final String COLUMN_HREF = "href";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_CULTURE = "culture";

        public static Uri buildAgencyUri(long _id) {
            return ContentUris.withAppendedId(CONTENT_URI, _id);
        }

    }

    public static final class BusStopEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_BUS_STOP).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BUS_STOP;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BUS_STOP;

        // Table name
        public static final String TABLE_NAME = "bus_stops";

        public static final String COLUMN_ID = "id";
        public static final String COLUMN_HREF = "href";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_CODE = "code";
        public static final String COLUMN_GEOMETRY_TYPE = "geometry_type";
        public static final String COLUMN_GEOMETRY_LATITUDE = "geometry_latitude";
        public static final String COLUMN_GEOMETRY_LONGITUDE = "geometry_longitude";
        public static final String COLUMN_MODES = "modes";

        public static Uri buildBusStopUri(long _id) {
            return ContentUris.withAppendedId(CONTENT_URI, _id);
        }

    }

    public static final class GeometryEntry implements BaseColumns {


        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_GEOMETRY).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GEOMETRY;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GEOMETRY;


        // Table name
        public static final String TABLE_NAME = "geometry";
        public static final String COLUMN_ID = "id";


    }

    public static final class FareProductEntry implements BaseColumns {


        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FARE_PRODUCT).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FARE_PRODUCT;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FARE_PRODUCT;

        // Table name
        public static final String TABLE_NAME = "fare_product";


        public static final String COLUMN_ID = "id";
        public static final String COLUMN_HREF = "href";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_AGENCY_ID = "agency_id";
        public static final String COLUMN_IS_DEFAULT = "isDefault";

        public static Uri buildFareProductUri(long _id) {
            return ContentUris.withAppendedId(CONTENT_URI, _id);
        }

    }

    public static final class FavoritesBusEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVOURITES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVOURITES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVOURITES;

        // Table name
        public static final String TABLE_NAME = "favourites_bus_stops";

        public static final String COLUMN_ID = "id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DATE_CREATED = "date_created";

        public static Uri buildFavouriteBusStopUri(long _id) {
            return ContentUris.withAppendedId(CONTENT_URI, _id);
        }
    }


}

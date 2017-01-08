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
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FARES).build();

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


}

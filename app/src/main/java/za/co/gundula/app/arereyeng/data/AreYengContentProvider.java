package za.co.gundula.app.arereyeng.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by kgundula on 2016/12/24.
 */

public class AreYengContentProvider extends ContentProvider {


    private static final UriMatcher aUriMatcher = buildUriMatcher();
    private AreYengDbHelper mOpenHelper;


    static final int FARES = 100;
    static final int FARES_ID = 101;
    static final int AGENCY = 200;
    static final int AGENCY_ID = 201;
    static final int BUS_STOP = 300;
    static final int BUS_STOP_ID = 301;
    static final int PRODUCT_FARE = 400;
    static final int PRODUCT_FARE_ID = 401;


    static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = AreYengContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, AreYengContract.PATH_FARES, FARES);
        matcher.addURI(authority, AreYengContract.PATH_FARES + "/*", FARES_ID);
        matcher.addURI(authority, AreYengContract.PATH_AGENCY, AGENCY);
        matcher.addURI(authority, AreYengContract.PATH_AGENCY + "/*", AGENCY_ID);
        matcher.addURI(authority, AreYengContract.PATH_BUS_STOP, BUS_STOP);
        matcher.addURI(authority, AreYengContract.PATH_BUS_STOP + "/*", BUS_STOP_ID);
        matcher.addURI(authority, AreYengContract.PATH_FARE_PRODUCT, PRODUCT_FARE);
        matcher.addURI(authority, AreYengContract.PATH_FARE_PRODUCT + "/*", PRODUCT_FARE_ID);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new AreYengDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (aUriMatcher.match(uri)) {

            // "fares"
            case FARES: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        AreYengContract.FaresEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case AGENCY: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        AreYengContract.AgencyEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case BUS_STOP: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        AreYengContract.BusStopEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case PRODUCT_FARE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        AreYengContract.FareProductEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = aUriMatcher.match(uri);

        switch (match) {
            case FARES_ID:
                return AreYengContract.FaresEntry.CONTENT_ITEM_TYPE;
            case FARES:
                return AreYengContract.FaresEntry.CONTENT_TYPE;
            case AGENCY_ID:
                return AreYengContract.AgencyEntry.CONTENT_ITEM_TYPE;
            case AGENCY:
                return AreYengContract.AgencyEntry.CONTENT_TYPE;
            case BUS_STOP_ID:
                return AreYengContract.BusStopEntry.CONTENT_ITEM_TYPE;
            case BUS_STOP:
                return AreYengContract.BusStopEntry.CONTENT_TYPE;
            case PRODUCT_FARE_ID:
                return AreYengContract.FareProductEntry.CONTENT_ITEM_TYPE;
            case PRODUCT_FARE:
                return AreYengContract.FareProductEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = aUriMatcher.match(uri);
        Uri aReYengUri;
        switch (match) {
            case FARES: {
                long _id = db.insert(AreYengContract.FaresEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    aReYengUri = AreYengContract.FaresEntry.buildFaresUri(_id);
                else
                    throw new SQLException("Failed to insert new row into :" + uri);
                break;
            }

            case AGENCY: {
                long _id = db.insert(AreYengContract.AgencyEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    aReYengUri = AreYengContract.AgencyEntry.buildAgencyUri(_id);
                else
                    throw new SQLException("Failed to insert new row into :" + uri);
                break;
            }
            case BUS_STOP: {
                long _id = db.insert(AreYengContract.BusStopEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    aReYengUri = AreYengContract.BusStopEntry.buildBusStopUri(_id);
                else
                    throw new SQLException("Failed to insert new row into :" + uri);
                break;
            }

            case PRODUCT_FARE: {
                long _id = db.insert(AreYengContract.FareProductEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    aReYengUri = AreYengContract.FareProductEntry.buildFareProductUri(_id);
                else
                    throw new SQLException("Failed to insert new row into :" + uri);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown Uri" + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return aReYengUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = aUriMatcher.match(uri);
        int deleted;

        switch (match) {
            case AGENCY:
                deleted = db.delete(AreYengContract.AgencyEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case FARES:
                deleted = db.delete(AreYengContract.FaresEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BUS_STOP:
                deleted = db.delete(AreYengContract.BusStopEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCT_FARE:
                deleted = db.delete(AreYengContract.FareProductEntry.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown Uri:" + uri);

        }

        if (selection == null || deleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return deleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = aUriMatcher.match(uri);
        int updated;

        switch (match) {
            case AGENCY: {
                updated = db.update(AreYengContract.AgencyEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case FARES: {
                updated = db.update(AreYengContract.FaresEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case BUS_STOP: {
                updated = db.update(AreYengContract.BusStopEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case PRODUCT_FARE: {
                updated = db.update(AreYengContract.FareProductEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (updated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return updated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = aUriMatcher.match(uri);
        switch (match) {
            case BUS_STOP:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(AreYengContract.BusStopEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

}

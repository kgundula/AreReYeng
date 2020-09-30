package za.co.gundula.app.arereyeng.data

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.SQLException
import android.net.Uri

/**
 * Created by kgundula on 2016/12/24.
 */
class AreYengContentProvider : ContentProvider() {
    private var mOpenHelper: AreYengDbHelper? = null
    override fun onCreate(): Boolean {
        mOpenHelper = AreYengDbHelper(context)
        return true
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        val retCursor: Cursor
        retCursor = when (aUriMatcher.match(uri)) {
            FARES -> {
                mOpenHelper!!.readableDatabase.query(
                        AreYengContract.FaresEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                )
            }
            AGENCY -> {
                mOpenHelper!!.readableDatabase.query(
                        AreYengContract.AgencyEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                )
            }
            BUS_STOP -> {
                mOpenHelper!!.readableDatabase.query(
                        AreYengContract.BusStopEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                )
            }
            PRODUCT_FARE -> {
                mOpenHelper!!.readableDatabase.query(
                        AreYengContract.FareProductEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                )
            }
            FAVOURITES_STOPS -> {
                mOpenHelper!!.readableDatabase.query(
                        AreYengContract.FavoritesBusEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                )
            }
            else -> throw UnsupportedOperationException("Unknown uri: $uri")
        }
        retCursor.setNotificationUri(context!!.contentResolver, uri)
        return retCursor
    }

    override fun getType(uri: Uri): String? {
        val match = aUriMatcher.match(uri)
        return when (match) {
            FARES_ID -> AreYengContract.FaresEntry.CONTENT_ITEM_TYPE
            FARES -> AreYengContract.FaresEntry.CONTENT_TYPE
            AGENCY_ID -> AreYengContract.AgencyEntry.CONTENT_ITEM_TYPE
            AGENCY -> AreYengContract.AgencyEntry.CONTENT_TYPE
            BUS_STOP_ID -> AreYengContract.BusStopEntry.CONTENT_ITEM_TYPE
            BUS_STOP -> AreYengContract.BusStopEntry.CONTENT_TYPE
            PRODUCT_FARE_ID -> AreYengContract.FareProductEntry.CONTENT_ITEM_TYPE
            PRODUCT_FARE -> AreYengContract.FareProductEntry.CONTENT_TYPE
            FAVOURITES_STOPS_ID -> AreYengContract.FavoritesBusEntry.CONTENT_ITEM_TYPE
            FAVOURITES_STOPS -> AreYengContract.FavoritesBusEntry.CONTENT_TYPE
            else -> throw UnsupportedOperationException("Unknown uri: $uri")
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val db = mOpenHelper!!.writableDatabase
        val match = aUriMatcher.match(uri)
        val aReYengUri: Uri
        aReYengUri = when (match) {
            FARES -> {
                val _id = db.insert(AreYengContract.FaresEntry.TABLE_NAME, null, values)
                if (_id > 0) AreYengContract.FaresEntry.buildFaresUri(_id) else throw SQLException("Failed to insert new row into :$uri")
            }
            AGENCY -> {
                val _id = db.insert(AreYengContract.AgencyEntry.TABLE_NAME, null, values)
                if (_id > 0) AreYengContract.AgencyEntry.buildAgencyUri(_id) else throw SQLException("Failed to insert new row into :$uri")
            }
            BUS_STOP -> {
                val _id = db.insert(AreYengContract.BusStopEntry.TABLE_NAME, null, values)
                if (_id > 0) AreYengContract.BusStopEntry.buildBusStopUri(_id) else throw SQLException("Failed to insert new row into :$uri")
            }
            PRODUCT_FARE -> {
                val _id = db.insert(AreYengContract.FareProductEntry.TABLE_NAME, null, values)
                if (_id > 0) AreYengContract.FareProductEntry.buildFareProductUri(_id) else throw SQLException("Failed to insert new row into :$uri")
            }
            FAVOURITES_STOPS -> {
                val _id = db.insert(AreYengContract.FavoritesBusEntry.TABLE_NAME, null, values)
                if (_id > 0) AreYengContract.FavoritesBusEntry.buildFavouriteBusStopUri(_id) else throw SQLException("Failed to insert new row into :$uri")
            }
            else -> throw UnsupportedOperationException("Unknown Uri$uri")
        }
        context!!.contentResolver.notifyChange(uri, null)
        return aReYengUri
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val db = mOpenHelper!!.writableDatabase
        val match = aUriMatcher.match(uri)
        val deleted: Int
        deleted = when (match) {
            AGENCY -> db.delete(AreYengContract.AgencyEntry.TABLE_NAME, selection, selectionArgs)
            FARES -> db.delete(AreYengContract.FaresEntry.TABLE_NAME, selection, selectionArgs)
            BUS_STOP -> db.delete(AreYengContract.BusStopEntry.TABLE_NAME, selection, selectionArgs)
            PRODUCT_FARE -> db.delete(AreYengContract.FareProductEntry.TABLE_NAME, selection, selectionArgs)
            FAVOURITES_STOPS -> db.delete(AreYengContract.FavoritesBusEntry.TABLE_NAME, selection, selectionArgs)
            else -> throw UnsupportedOperationException("Unknown Uri:$uri")
        }
        if (selection == null || deleted != 0) {
            context!!.contentResolver.notifyChange(uri, null)
        }
        return deleted
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        val db = mOpenHelper!!.writableDatabase
        val match = aUriMatcher.match(uri)
        val updated: Int
        updated = when (match) {
            AGENCY -> {
                db.update(AreYengContract.AgencyEntry.TABLE_NAME, values, selection, selectionArgs)
            }
            FARES -> {
                db.update(AreYengContract.FaresEntry.TABLE_NAME, values, selection, selectionArgs)
            }
            BUS_STOP -> {
                db.update(AreYengContract.BusStopEntry.TABLE_NAME, values, selection, selectionArgs)
            }
            PRODUCT_FARE -> {
                db.update(AreYengContract.FareProductEntry.TABLE_NAME, values, selection, selectionArgs)
            }
            FAVOURITES_STOPS -> {
                db.update(AreYengContract.FavoritesBusEntry.TABLE_NAME, values, selection, selectionArgs)
            }
            else -> throw UnsupportedOperationException("Unknown uri: $uri")
        }
        if (updated != 0) {
            context!!.contentResolver.notifyChange(uri, null)
        }
        return updated
    }

    override fun bulkInsert(uri: Uri, values: Array<ContentValues>): Int {
        val db = mOpenHelper!!.writableDatabase
        val match = aUriMatcher.match(uri)
        return when (match) {
            BUS_STOP -> {
                db.beginTransaction()
                var returnCount = 0
                try {
                    for (value in values) {
                        val _id = db.insert(AreYengContract.BusStopEntry.TABLE_NAME, null, value)
                        if (_id != -1L) {
                            returnCount++
                        }
                    }
                    db.setTransactionSuccessful()
                } finally {
                    db.endTransaction()
                }
                context!!.contentResolver.notifyChange(uri, null)
                returnCount
            }
            else -> super.bulkInsert(uri, values)
        }
    }

    companion object {
        private val aUriMatcher = buildUriMatcher()
        const val FARES = 100
        const val FARES_ID = 101
        const val AGENCY = 200
        const val AGENCY_ID = 201
        const val BUS_STOP = 300
        const val BUS_STOP_ID = 301
        const val PRODUCT_FARE = 400
        const val PRODUCT_FARE_ID = 401
        const val FAVOURITES_STOPS = 500
        const val FAVOURITES_STOPS_ID = 501
        fun buildUriMatcher(): UriMatcher {
            // I know what you're thinking.  Why create a UriMatcher when you can use regular
            // expressions instead?  Because you're not crazy, that's why.

            // All paths added to the UriMatcher have a corresponding code to return when a match is
            // found.  The code passed into the constructor represents the code to return for the root
            // URI.  It's common to use NO_MATCH as the code for this case.
            val matcher = UriMatcher(UriMatcher.NO_MATCH)
            val authority = AreYengContract.CONTENT_AUTHORITY

            // For each type of URI you want to add, create a corresponding code.
            matcher.addURI(authority, AreYengContract.PATH_FARES, FARES)
            matcher.addURI(authority, AreYengContract.PATH_FARES + "/*", FARES_ID)
            matcher.addURI(authority, AreYengContract.PATH_AGENCY, AGENCY)
            matcher.addURI(authority, AreYengContract.PATH_AGENCY + "/*", AGENCY_ID)
            matcher.addURI(authority, AreYengContract.PATH_BUS_STOP, BUS_STOP)
            matcher.addURI(authority, AreYengContract.PATH_BUS_STOP + "/*", BUS_STOP_ID)
            matcher.addURI(authority, AreYengContract.PATH_FARE_PRODUCT, PRODUCT_FARE)
            matcher.addURI(authority, AreYengContract.PATH_FARE_PRODUCT + "/*", PRODUCT_FARE_ID)
            matcher.addURI(authority, AreYengContract.PATH_FAVOURITES, FAVOURITES_STOPS)
            matcher.addURI(authority, AreYengContract.PATH_FAVOURITES + "/*", FAVOURITES_STOPS_ID)
            return matcher
        }
    }
}
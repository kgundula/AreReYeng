package za.co.gundula.app.arereyeng.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by kgundula on 2016/12/24.
 */

public class AreYengDbHelper extends SQLiteOpenHelper {


    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "areyeng.db";

    public AreYengDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {


        final String SQL_CREATE_FARES_TABLE = "CREATE TABLE " + AreYengContract.FaresEntry.TABLE_NAME + "(" +
                AreYengContract.FaresEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                AreYengContract.FaresEntry.COLUMN_FARE_PRODUCT + " TEXT UNIQUE NOT NULL, " +
                AreYengContract.FaresEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                AreYengContract.FaresEntry.COLUMN_COST + " REAL NOT NULL, " +
                AreYengContract.FaresEntry.COLUMN_MESSAGES + " TEXT NOT NULL " +
                " );";


        final String SQL_CREATE_AGENCY_TABLE = "CREATE TABLE " + AreYengContract.AgencyEntry.TABLE_NAME + "(" +
                AreYengContract.AgencyEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                AreYengContract.AgencyEntry.COLUMN_ID + " TEXT UNIQUE NOT NULL, " +
                AreYengContract.AgencyEntry.COLUMN_HREF + " TEXT NOT NULL, " +
                AreYengContract.AgencyEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                AreYengContract.AgencyEntry.COLUMN_CULTURE + " TEXT NOT NULL, " +
                " UNIQUE ( " + AreYengContract.AgencyEntry.COLUMN_ID + " ) ON CONFLICT IGNORE" +
                " );";


        db.execSQL(SQL_CREATE_FARES_TABLE);
        db.execSQL(SQL_CREATE_AGENCY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + AreYengContract.FaresEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + AreYengContract.AgencyEntry.TABLE_NAME);
        onCreate(db);
    }
}

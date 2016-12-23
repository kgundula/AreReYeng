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


        final String SQL_CREATE_FARES_TABLE = "CREATE TABLE " + AreYengContract.FaresEntry.TABLE_NAME + " (" +
                AreYengContract.FaresEntry._ID + " INTEGER PRIMARY KEY," +
                AreYengContract.FaresEntry.COLUMN_FARE_PRODUCT + " TEXT UNIQUE NOT NULL, " +
                AreYengContract.FaresEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                AreYengContract.FaresEntry.COLUMN_COST + " REAL NOT NULL, " +
                AreYengContract.FaresEntry.COLUMN_MESSAGES + " TEXT NOT NULL " +
                " );";

        db.execSQL(SQL_CREATE_FARES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + AreYengContract.FaresEntry.TABLE_NAME);
        onCreate(db);
    }
}

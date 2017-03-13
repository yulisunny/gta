package ca.cvst.gta.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ca.cvst.gta.db.GraphContract.GraphEntry;
import ca.cvst.gta.db.TtcNotificationContract.TtcNotificationEntry;

public class DbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 6;
    public static final String DATABASE_NAME = "cvst_gta.db";
    private static final String CREATE_HISTORICAL_GRAPHS_TABLE =
            "CREATE TABLE " + GraphEntry.TABLE_NAME + " (" +
                    GraphEntry._ID + " INTEGER PRIMARY KEY," +
                    GraphEntry.GRAPH_ID + " INTEGER," +
                    GraphEntry.DATA_TYPE + " TEXT," +
                    GraphEntry.TIMESTAMP + " INTEGER," +
                    GraphEntry.DATA_TIME + " TEXT, " +
                    GraphEntry.TIME_STEPS + " TEXT," +
                    GraphEntry.CHART_TYPE + " TEXT," +
                    GraphEntry.START_TIME + " INTEGER," +
                    GraphEntry.END_TIME + " INTEGER," +
                    GraphEntry.LINK_ID + " TEXT, " +
                    GraphEntry.DATA_LIST + " TEXT)";

    private static final String CREATE_TTC_NOTIFICATIONS_TABLE =
            "CREATE TABLE " + TtcNotificationEntry.TABLE_NAME + " (" +
                    TtcNotificationEntry._ID + " INTEGER PRIMARY KEY," +
                    TtcNotificationEntry.TIMESTAMP + " INTEGER," +
                    TtcNotificationEntry.DIR_TAG + " TEXT," +
                    TtcNotificationEntry.NAME + " TEXT," +
                    TtcNotificationEntry.GPS_TIME + " INTEGER," +
                    TtcNotificationEntry.LAST_TIME + " TEXT," +
                    TtcNotificationEntry.LATITUDE + " REAL," +
                    TtcNotificationEntry.LONGITUDE + " REAL," +
                    TtcNotificationEntry.DATE_TIME + " TEXT," +
                    TtcNotificationEntry.HEADING + " TEXT," +
                    TtcNotificationEntry.PREDICTABLE + " INTEGER," +
                    TtcNotificationEntry.ROUTE_NUMBER + " TEXT," +
                    TtcNotificationEntry.SUBSCRIPTION_IDS + " TEXT)";


    private static final String DROP_HISTORICAL_GRAPHS_TABLE =
            "DROP TABLE IF EXISTS " + GraphEntry.TABLE_NAME;
    private static final String DROP_TTC_NOTIFICATIONS_TABLE =
            "DROP TABLE IF EXISTS " + TtcNotificationEntry.TABLE_NAME;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_HISTORICAL_GRAPHS_TABLE);
        db.execSQL(CREATE_TTC_NOTIFICATIONS_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(DROP_HISTORICAL_GRAPHS_TABLE);
        db.execSQL(DROP_TTC_NOTIFICATIONS_TABLE);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}

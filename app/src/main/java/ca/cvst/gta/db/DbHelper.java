package ca.cvst.gta.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ca.cvst.gta.db.AirsenseNotificationsContract.AirsenseNotificationEntry;
import ca.cvst.gta.db.AirsenseSubscriptionsContract.AirsenseSubscriptionEntry;
import ca.cvst.gta.db.GraphContract.GraphEntry;
import ca.cvst.gta.db.SubscriptionsContract.SubscriptionEntry;
import ca.cvst.gta.db.TtcNotificationContract.TtcNotificationEntry;
import ca.cvst.gta.db.TtcSubscriptionsContract.TtcSubscriptionEntry;

public class DbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 18;
    public static final String DATABASE_NAME = "cvst_gta.db";
    private static final String CREATE_HISTORICAL_GRAPHS_TABLE =
            "CREATE TABLE " + GraphEntry.TABLE_NAME + " (" +
                    GraphEntry._ID + " INTEGER PRIMARY KEY," +
                    GraphEntry.GRAPH_ID + " INTEGER," +
                    GraphEntry.DATA_TYPE + " TEXT," +
                    GraphEntry.TIMESTAMP + " INTEGER," +
                    GraphEntry.DATA_TIME + " INTEGER, " +
                    GraphEntry.TIME_STEPS + " TEXT," +
                    GraphEntry.CHART_TYPE + " TEXT," +
                    GraphEntry.START_TIME + " INTEGER," +
                    GraphEntry.END_TIME + " INTEGER," +
                    GraphEntry.LINK_ID + " TEXT, " +
                    GraphEntry.DATA_LIST + " TEXT)";

    private static final String CREATE_SUBSCRIPTIONS_TABLE =
            "CREATE TABLE " + SubscriptionEntry.TABLE_NAME + " (" +
                    SubscriptionEntry._ID + " INTEGER PRIMARY KEY," +
                    SubscriptionEntry.TIMESTAMP + " INTEGER," +
                    SubscriptionEntry.NAME + " TEXT," +
                    SubscriptionEntry.UPPER_LATITUDE + " REAL," +
                    SubscriptionEntry.UPPER_LONGITUDE + " REAL," +
                    SubscriptionEntry.LOWER_LATITUDE + " REAL," +
                    SubscriptionEntry.LOWER_LONGITUDE + " REAL," +
                    SubscriptionEntry.TYPE + " TEXT," +
                    SubscriptionEntry.FILTERS + " TEXT," +
                    SubscriptionEntry.MONDAY + " INTEGER," +
                    SubscriptionEntry.TUESDAY + " INTEGER," +
                    SubscriptionEntry.WEDNESDAY + " INTEGER," +
                    SubscriptionEntry.THURSDAY + " INTEGER," +
                    SubscriptionEntry.FRIDAY + " INTEGER," +
                    SubscriptionEntry.SATURDAY + " INTEGER," +
                    SubscriptionEntry.SUNDAY + " INTEGER," +
                    SubscriptionEntry.START_TIME + " INTEGER," +
                    SubscriptionEntry.END_TIME + " INTEGER," +
                    SubscriptionEntry.NOTIFICATION_ENABLED + " INTEGER," +
                    SubscriptionEntry.SUBSCRIPTION_TYPE + " TEXT," +
                    SubscriptionEntry.SUBSCRIPTION_ID + " TEXT UNIQUE)";

    private static final String CREATE_TTC_NOTIFICATIONS_TABLE =
            "CREATE TABLE " + TtcNotificationEntry.TABLE_NAME + " (" +
                    TtcNotificationEntry._ID + " INTEGER PRIMARY KEY," +
                    TtcNotificationEntry.BUS_ID + " INTEGER," +
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

    private static final String CREATE_AIRSENSE_NOTIFICATIONS_TABLE =
            "CREATE TABLE " + AirsenseNotificationEntry.TABLE_NAME + " (" +
                    AirsenseNotificationEntry._ID + " INTEGER PRIMARY KEY," +
                    AirsenseNotificationEntry.TIMESTAMP + " INTEGER," +
                    AirsenseNotificationEntry.MONITOR_NAME + " TEXT," +
                    AirsenseNotificationEntry.DATE + " TEXT," +
                    AirsenseNotificationEntry.LATITUDE + " REAL," +
                    AirsenseNotificationEntry.LONGITUDE + " REAL," +
                    AirsenseNotificationEntry.NOX + " REAL," +
                    AirsenseNotificationEntry.O3 + " REAL," +
                    AirsenseNotificationEntry.CO2 + " REAL," +
                    AirsenseNotificationEntry.AQHI + " REAL," +
                    AirsenseNotificationEntry.PM + " REAL," +
                    AirsenseNotificationEntry.AH + " REAL," +
                    AirsenseNotificationEntry.CO + " REAL," +
                    AirsenseNotificationEntry.COO + " REAL," +
                    AirsenseNotificationEntry.ADDRESS + " TEXT," +
                    AirsenseNotificationEntry.SUBSCRIPTION_IDS + " TEXT)";

    private static final String CREATE_TTC_SUBSCRIPTIONS_TABLE =
            "CREATE TABLE " + TtcSubscriptionEntry.TABLE_NAME + " (" +
                    TtcSubscriptionEntry._ID + " INTEGER PRIMARY KEY," +
                    TtcSubscriptionEntry.TIMESTAMP + " INTEGER," +
                    TtcSubscriptionEntry.NAME + " TEXT," +
                    TtcSubscriptionEntry.UPPER_LATITUDE + " REAL," +
                    TtcSubscriptionEntry.UPPER_LONGITUDE + " REAL," +
                    TtcSubscriptionEntry.LOWER_LATITUDE + " REAL," +
                    TtcSubscriptionEntry.LOWER_LONGITUDE + " REAL," +
                    TtcSubscriptionEntry.ROUTE_NUMBER + " TEXT," +
                    TtcSubscriptionEntry.MONDAY + " INTEGER," +
                    TtcSubscriptionEntry.TUESDAY + " INTEGER," +
                    TtcSubscriptionEntry.WEDNESDAY + " INTEGER," +
                    TtcSubscriptionEntry.THURSDAY + " INTEGER," +
                    TtcSubscriptionEntry.FRIDAY + " INTEGER," +
                    TtcSubscriptionEntry.SATURDAY + " INTEGER," +
                    TtcSubscriptionEntry.SUNDAY + " INTEGER," +
                    TtcSubscriptionEntry.START_TIME + " INTEGER," +
                    TtcSubscriptionEntry.END_TIME + " INTEGER," +
                    TtcSubscriptionEntry.NOTIFICATION_ENABLED + " INTEGER," +
                    TtcSubscriptionEntry.SUBSCRIPTION_TYPE + " TEXT," +
                    TtcSubscriptionEntry.SUBSCRIPTION_ID + " TEXT)";

    private static final String CREATE_AIRSENSE_SUBSCRIPTIONS_TABLE =
            "CREATE TABLE " + AirsenseSubscriptionEntry.TABLE_NAME + " (" +
                    AirsenseSubscriptionEntry._ID + " INTEGER PRIMARY KEY," +
                    AirsenseSubscriptionEntry.TIMESTAMP + " INTEGER," +
                    AirsenseSubscriptionEntry.NAME + " TEXT," +
                    AirsenseSubscriptionEntry.UPPER_LATITUDE + " REAL," +
                    AirsenseSubscriptionEntry.UPPER_LONGITUDE + " REAL," +
                    AirsenseSubscriptionEntry.LOWER_LATITUDE + " REAL," +
                    AirsenseSubscriptionEntry.LOWER_LONGITUDE + " REAL," +
                    AirsenseSubscriptionEntry.AIR_TYPE + " TEXT," +
                    AirsenseSubscriptionEntry.AIR_VALUE + " REAL," +
                    AirsenseSubscriptionEntry.MONDAY + " INTEGER," +
                    AirsenseSubscriptionEntry.TUESDAY + " INTEGER," +
                    AirsenseSubscriptionEntry.WEDNESDAY + " INTEGER," +
                    AirsenseSubscriptionEntry.THURSDAY + " INTEGER," +
                    AirsenseSubscriptionEntry.FRIDAY + " INTEGER," +
                    AirsenseSubscriptionEntry.SATURDAY + " INTEGER," +
                    AirsenseSubscriptionEntry.SUNDAY + " INTEGER," +
                    AirsenseSubscriptionEntry.START_TIME + " INTEGER," +
                    AirsenseSubscriptionEntry.END_TIME + " INTEGER," +
                    AirsenseSubscriptionEntry.NOTIFICATION_ENABLED + " INTEGER," +
                    AirsenseSubscriptionEntry.SUBSCRIPTION_TYPE + " TEXT," +
                    AirsenseSubscriptionEntry.SUBSCRIPTION_ID + " TEXT)";

    private static final String DROP_HISTORICAL_GRAPHS_TABLE =
            "DROP TABLE IF EXISTS " + GraphEntry.TABLE_NAME;
    private static final String DROP_SUBSCRIPTIONS_TABLE =
            "DROP TABLE IF EXISTS " + SubscriptionEntry.TABLE_NAME;
    private static final String DROP_TTC_NOTIFICATIONS_TABLE =
            "DROP TABLE IF EXISTS " + TtcNotificationEntry.TABLE_NAME;
    private static final String DROP_AIRSENSE_NOTIFICATIONS_TABLE =
            "DROP TABLE IF EXISTS " + AirsenseNotificationEntry.TABLE_NAME;
    private static final String DROP_TTC_SUBSCRIPTIONS_TABLE =
            "DROP TABLE IF EXISTS " + TtcSubscriptionEntry.TABLE_NAME;
    private static final String DROP_AIRSENSE_SUBSCRIPTIONS_TABLE =
            "DROP TABLE IF EXISTS " + AirsenseSubscriptionEntry.TABLE_NAME;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_HISTORICAL_GRAPHS_TABLE);
        db.execSQL(CREATE_SUBSCRIPTIONS_TABLE);
        db.execSQL(CREATE_TTC_NOTIFICATIONS_TABLE);
        db.execSQL(CREATE_AIRSENSE_NOTIFICATIONS_TABLE);
        db.execSQL(CREATE_TTC_SUBSCRIPTIONS_TABLE);
        db.execSQL(CREATE_AIRSENSE_SUBSCRIPTIONS_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(DROP_HISTORICAL_GRAPHS_TABLE);
        db.execSQL(DROP_SUBSCRIPTIONS_TABLE);
        db.execSQL(DROP_TTC_NOTIFICATIONS_TABLE);
        db.execSQL(DROP_AIRSENSE_NOTIFICATIONS_TABLE);
        db.execSQL(DROP_TTC_SUBSCRIPTIONS_TABLE);
        db.execSQL(DROP_AIRSENSE_SUBSCRIPTIONS_TABLE);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}

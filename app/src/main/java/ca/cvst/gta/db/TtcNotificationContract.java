package ca.cvst.gta.db;

import android.provider.BaseColumns;

public class TtcNotificationContract {

    private TtcNotificationContract() {
    }

    public static class TtcNotificationEntry implements BaseColumns {
        public static final String TABLE_NAME = "ttc_notifications";
        public static final String TIMESTAMP = "timestamp";
        public static final String DIR_TAG = "dir_tag";
        public static final String NAME = "name";
        public static final String GPS_TIME = "gps_time";
        public static final String LAST_TIME = "last_time";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String DATE_TIME = "date_time";
        public static final String HEADING = "heading";
        public static final String PREDICTABLE = "predictable";
        public static final String ROUTE_NUMBER = "route_number";
        public static final String SUBSCRIPTION_IDS = "subscription_ids";
    }
}

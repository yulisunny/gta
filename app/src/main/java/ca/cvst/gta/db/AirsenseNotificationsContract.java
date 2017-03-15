package ca.cvst.gta.db;

import android.provider.BaseColumns;

public class AirsenseNotificationsContract {

    public static class AirsenseNotificationEntry implements BaseColumns {
        public static final String TABLE_NAME = "airsense_notifications";
        public static final String TIMESTAMP = "timestamp";
        public static final String MONITOR_NAME = "monitor_name";
        public static final String DATE = "date";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String NOX = "nox";
        public static final String O3 = "o3";
        public static final String CO2 = "co2";
        public static final String AQHI = "aqhi";
        public static final String PM = "pm";
        public static final String AH = "ah";
        public static final String CO = "co";
        public static final String COO = "coo";
        public static final String ADDRESS = "address";
        public static final String SUBSCRIPTION_IDS = "subscription_ids";
    }
}

package ca.cvst.gta.db;

import android.provider.BaseColumns;

public class TtcSubscriptionsContract {
    public static class TtcSubscriptionEntry implements BaseColumns {
        public static final String TABLE_NAME = "ttc_subscriptions";
        public static final String TIMESTAMP = "timestamp";
        public static final String NAME = "name";
        public static final String UPPER_LATITUDE = "upper_latitude";
        public static final String UPPER_LONGITUDE = "upper_longitude";
        public static final String LOWER_LATITUDE = "lower_latitude";
        public static final String LOWER_LONGITUDE = "lower_longitude";
        public static final String ROUTE_NUMBER = "route_number";
        public static final String SUBSCRIPTION_ID = "subscription_id";
    }
}

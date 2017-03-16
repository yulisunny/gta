package ca.cvst.gta.db;

import android.provider.BaseColumns;

public class AirsenseSubscriptionsContract {
    public static class AirsenseSubscriptionEntry implements BaseColumns {
        public static final String TABLE_NAME = "airsense_subscriptions";
        public static final String TIMESTAMP = "timestamp";
        public static final String NAME = "name";
        public static final String UPPER_LATITUDE = "upper_latitude";
        public static final String UPPER_LONGITUDE = "upper_longitude";
        public static final String LOWER_LATITUDE = "lower_latitude";
        public static final String LOWER_LONGITUDE = "lower_longitude";
        public static final String AIR_TYPE = "air_type";
        public static final String AIR_VALUE = "air_value";
        public static final String MONDAY = "monday";
        public static final String TUESDAY = "tuesday";
        public static final String WEDNESDAY = "wednesday";
        public static final String THURSDAY = "thursday";
        public static final String FRIDAY = "friday";
        public static final String SATURDAY = "saturday";
        public static final String SUNDAY = "sunday";
        public static final String START_TIME = "start_time";
        public static final String END_TIME = "end_time";
        public static final String NOTIFICATION_ENABLED = "notification_enabled";
        public static final String SUBSCRIPTION_TYPE = "subscription_type";
        public static final String SUBSCRIPTION_ID = "subscription_id";
    }
}

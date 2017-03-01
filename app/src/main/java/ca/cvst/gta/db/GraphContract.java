package ca.cvst.gta.db;

import android.provider.BaseColumns;

public final class GraphContract {

    private GraphContract() {}

    /* Inner class that defines the table contents */
    public static class GraphEntry implements BaseColumns {
        public static final String TABLE_NAME = "historical_graphs";
        public static final String GRAPH_ID = "graph_id";
        public static final String DATA_TYPE = "data_type";
        public static final String CHART_TYPE = "chart_type";

        // GSON serialized strings
        public static final String TIME_STEPS = "time_steps";
        public static final String DATA_LIST = "data_list";
        public static final String DATA_TIME = "data_time";

        // Last updated timestamp
        public static final String TIMESTAMP = "timestamp";

        //ISO DATE TIME?
        public static final String START_TIME = "start_time";
        public static final String END_TIME = "end_time";

        public static final String LINK_ID = "link_id";
        public static final String LINK_ADDRESS= "link_addr";
    }

}

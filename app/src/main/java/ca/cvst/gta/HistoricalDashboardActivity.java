package ca.cvst.gta;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.joda.time.LocalDateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ca.cvst.gta.db.DbHelper;
import ca.cvst.gta.db.GraphContract;

public class HistoricalDashboardActivity extends AppCompatActivity {

    private static final int NEW_HISTORICAL_CHART_REQUEST = 1;

    private ArrayAdapter<HistoricalChartData> historicalChartAdapter;
    private ArrayList<HistoricalChartData> historicalChartList;
    private DbHelper mDbHelper;
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historical_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton newChartBtn = (FloatingActionButton) findViewById(R.id.btn_new_historical_chart);
        newChartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NewHistoricalChartActivity.class);
                startActivityForResult(intent, NEW_HISTORICAL_CHART_REQUEST);
            }
        });

        historicalChartList = new ArrayList<>();
        historicalChartAdapter = new HistoricalChartAdapter(this, historicalChartList);
        ListView listview = (ListView) findViewById(R.id.historical_dashboard_listview);
        listview.setAdapter(historicalChartAdapter);
        registerForContextMenu(listview);

        // Init database
        mDbHelper = new DbHelper(this);
        List<HistoricalChartData> existingGraphs = loadAllGraphsFromDB();
        for (HistoricalChartData graph : existingGraphs) {
            if (graphNeedsUpdate(graph)) {
                updateGraphInDB(graph);
            } else {
                historicalChartAdapter.add(graph);
            }
        }


    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.historical_dashboard_listview) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle(historicalChartList.get(info.position).mChartType);
            String[] menuItems = getResources().getStringArray(R.array.historical_dashboard_graph_menu_items);
            for (int i = 0; i < menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case 0: // EDIT
                return true;
            case 1: // Delete
                historicalChartAdapter.remove(historicalChartList.get(info.position));
                historicalChartAdapter.notifyDataSetChanged();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private Boolean graphNeedsUpdate(HistoricalChartData graph) {
        LocalDateTime dateTime = new LocalDateTime(graph.mLastUpdatedTime);
        LocalDateTime updateTime = dateTime.plusDays(1);
        LocalDateTime currTime = new LocalDateTime();
        return currTime.isAfter(updateTime);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Check which graphs needs to be saved
        for (int i = 0; i < historicalChartAdapter.getCount(); i++) {
            HistoricalChartData graph = historicalChartAdapter.getItem(i);
            HistoricalChartStatus status = graph.mStatus;
            switch (status) {
                case OKAY:
                    break;
                case NEEDS_PERSIST:
                    saveGraphsToDB(graph);
                    break;
                case NEEDS_UPDATE:
                    updateGraphInDB(graph);
            }
        }

        // Save the graphs
        mDbHelper.close();
    }

    private List<HistoricalChartData> loadAllGraphsFromDB() {
        System.out.println("HISTORICAL CHART LOADING A CHART!!!");
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                GraphContract.GraphEntry.GRAPH_ID,
                GraphContract.GraphEntry.CHART_TYPE,
                GraphContract.GraphEntry.DATA_TYPE,
                GraphContract.GraphEntry.DATA_TIME,
                GraphContract.GraphEntry.START_TIME,
                GraphContract.GraphEntry.END_TIME,
                GraphContract.GraphEntry.LINK_ID,
                GraphContract.GraphEntry.DATA_LIST,
                GraphContract.GraphEntry.TIME_STEPS
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = GraphContract.GraphEntry.DATA_TYPE + " = ?";
        String[] selectionArgs = {"My Title"};

        // How you want the results sorted in the resulting Cursor
        String sortOrder = GraphContract.GraphEntry.GRAPH_ID + " ASC";

        Cursor cursor = db.query(
                GraphContract.GraphEntry.TABLE_NAME,      // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        // Read from cursor
        List<HistoricalChartData> ret = new ArrayList<>();
        while (cursor.moveToNext()) {
            String graphId = cursor.getString(cursor.getColumnIndexOrThrow(GraphContract.GraphEntry.GRAPH_ID));
            String chartType = cursor.getString(cursor.getColumnIndex(GraphContract.GraphEntry.CHART_TYPE));
            String dataType = cursor.getString(cursor.getColumnIndex(GraphContract.GraphEntry.DATA_TYPE));
            String dataTime = cursor.getString(cursor.getColumnIndex(GraphContract.GraphEntry.DATA_TIME));
            Long startTime = cursor.getLong(cursor.getColumnIndex(GraphContract.GraphEntry.START_TIME));
            Long endTime = cursor.getLong(cursor.getColumnIndex(GraphContract.GraphEntry.END_TIME));
            String linkId = cursor.getString(cursor.getColumnIndex(GraphContract.GraphEntry.LINK_ID));

            List<Long> timeSteps = gson.fromJson(
                    cursor.getString(cursor.getColumnIndex(GraphContract.GraphEntry.TIME_STEPS)),
                    new TypeToken<ArrayList<Long>>() {
                    }.getType());
            List<Integer> dataList = gson.fromJson(
                    cursor.getString(cursor.getColumnIndex(GraphContract.GraphEntry.DATA_LIST)),
                    new TypeToken<ArrayList<Integer>>() {
                    }.getType());

            ret.add(new HistoricalChartData(chartType, dataType, startTime, endTime, dataTime,
                    dataList, timeSteps, graphId, HistoricalChartStatus.OKAY, linkId));
        }
        cursor.close();
        return ret;
    }

    private void deleteGraphFromDB() {
//        // Define 'where' part of query.
//        String selection = FeedEntry.COLUMN_NAME_TITLE + " LIKE ?";
//        // Specify arguments in placeholder order.
//                String[] selectionArgs = { "MyTitle" };
//        // Issue SQL statement.
//                db.delete(FeedEntry.TABLE_NAME, selection, selectionArgs);
    }

    private void saveGraphsToDB(HistoricalChartData graph) {
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(GraphContract.GraphEntry.DATA_TYPE, graph.mDataType);
        values.put(GraphContract.GraphEntry.DATA_LIST, gson.toJson(graph.mData));
        values.put(GraphContract.GraphEntry.TIME_STEPS, gson.toJson(graph.mTimeSteps));
        values.put(GraphContract.GraphEntry.TIMESTAMP, graph.mLastUpdatedTime);
        values.put(GraphContract.GraphEntry.GRAPH_ID, graph.mGraphId);
        values.put(GraphContract.GraphEntry.DATA_TIME, graph.mDataTime);
        values.put(GraphContract.GraphEntry.CHART_TYPE, graph.mChartType);
        values.put(GraphContract.GraphEntry.START_TIME, graph.mStartTime);
        values.put(GraphContract.GraphEntry.END_TIME, graph.mEndTime);
        values.put(GraphContract.GraphEntry.LINK_ID, graph.mLinkId);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(GraphContract.GraphEntry.TABLE_NAME, null, values);
    }

    private void updateGraphInDB(HistoricalChartData graph) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(GraphContract.GraphEntry.TIME_STEPS, gson.toJson(graph.mTimeSteps));
        values.put(GraphContract.GraphEntry.DATA_LIST, gson.toJson(graph.mData));
        values.put(GraphContract.GraphEntry.START_TIME, graph.mStartTime);
        values.put(GraphContract.GraphEntry.END_TIME, graph.mEndTime);

        // Which row to update, based on the title
        String selection = GraphContract.GraphEntry.GRAPH_ID + " LIKE ?";
        String[] selectionArgs = {graph.mGraphId};

        db.update(
                GraphContract.GraphEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NEW_HISTORICAL_CHART_REQUEST) {
            if (resultCode == RESULT_OK) {
                String chartType = data.getStringExtra("CHART_TYPE");
                String chartDataTime = data.getStringExtra("DATA_TIME");
                Long startTime = data.getLongExtra("START_TIME", 0);
                Long endTime = data.getLongExtra("END_TIME", 0);
                String linkId = data.getStringExtra("LINK_ID");
                Integer typePos = data.getIntExtra("DATA_TYPE_POS", 0);
                String[] hwDataTypes = getResources().getStringArray(R.array.new_historical_chart_graph_type);

                fetchDataFromCvst(linkId, hwDataTypes[typePos], chartType, chartDataTime, startTime, endTime);
            }
        }
    }

    private void createHistoricalChart(String chartType, String chartDataTime, Long startTime,
                                       Long endTime, List<Integer> chartData, List<Long> startTimeNeeded, String linkId) {
        historicalChartAdapter.add(new HistoricalChartData(
                chartType, chartType, startTime, endTime, chartDataTime, chartData,
                startTimeNeeded, null, HistoricalChartStatus.NEEDS_PERSIST, linkId
        ));
    }

    public void createLineChart(View v, HistoricalChartData chartData) {
        List<Integer> trafficData = chartData.mData;

        LineChart chart = (LineChart) v.findViewById(R.id.historical_dashboard_card_iv);

        // Set Axis
        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new DateAxisFormatter(chartData.mTimeSteps));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawLabels(true);

        YAxis yRightAxis = chart.getAxisRight();
        yRightAxis.setEnabled(false);

        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < trafficData.size(); i++) {
            entries.add(new Entry((float) i, (float) trafficData.get(i)));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Average Highway Speed");
        LineData lineData = new LineData(dataSet);
        lineData.setValueTextSize(12);

        // Update Chart Title
        Description description = new Description();
        description.setEnabled(false);
        chart.setDescription(description);

        Legend chartLegend = chart.getLegend();
        chartLegend.setEnabled(false);

        // Create Chart
        chart.setData(lineData);
        chart.invalidate();
    }

    private void updateDataFromCvst(final HistoricalChartData graph) {
        Long timeDelta = graph.mEndTime - graph.mStartTime;
        Long newEndTime = System.currentTimeMillis();
        Long newStartTime = newEndTime - timeDelta;

        final List<Long> startTimeNeeded = getStartTimeByDay(newStartTime, newEndTime);
        final List<Integer> chartData = new ArrayList<>();

        for (Long startDay : startTimeNeeded) {
            Long endDayTime = startDay + 60 * 1000;
            Long startTimeSecond = startDay / 1000;
            Long endTimeSecond = endDayTime / 1000;

            String url = "http://portal.cvst.ca/api/0.1/tomtom/hdf/nonfreeflowts1ts2/analyticsES?id=" + graph.mLinkId +
                    "&starttime=" + startTimeSecond.toString() + "&endtime=" + endTimeSecond.toString();
            JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            // Retrieve Data from CVST
                            if (isNonFreeflow(response)) {
                                chartData.add(parseNonFreeflowData(response));
                            } else {
                                chartData.add(parseNonFreeflowData(response));
                            }
                            System.out.println("DATA RECEIVED!!" + chartData.toString());
                            // Create new graph
                            if (chartData.size() == startTimeNeeded.size()) {
                                graph.mData = chartData;
                                graph.mTimeSteps = startTimeNeeded;
                                historicalChartAdapter.add(graph);
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("error = " + error);
                }
            });
            NetworkManager.getInstance(this).addToRequestQueue(jsonObjectRequest);
        }

    }

    private void fetchDataFromCvst(final String linkId, final String dataType, final String chartType,
                                   final String chartDataTime, final Long startTime, final Long endTime) {
        final List<Long> startTimeNeeded = getStartTimeByDay(startTime, endTime);
        final List<Integer> chartData = new ArrayList<>();

        for (Long startDay : startTimeNeeded) {
            Long endDayTime = startDay + 60 * 1000;
            Long startTimeSecond = startDay / 1000;
            Long endTimeSecond = endDayTime / 1000;

            String url = "http://portal.cvst.ca/api/0.1/tomtom/hdf/nonfreeflowts1ts2/analyticsES?id=" + linkId +
                    "&starttime=" + startTimeSecond.toString() + "&endtime=" + endTimeSecond.toString();
            JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            // Retrieve Data from CVST
                            if (isNonFreeflow(response)) {
                                chartData.add(parseNonFreeflowData(response));
                            } else {
                                chartData.add(parseNonFreeflowData(response));
                            }
                            System.out.println("DATA RECEIVED!!" + chartData.toString());
                            // Create new graph
                            if (chartData.size() == startTimeNeeded.size()) {
                                createHistoricalChart(chartType, chartDataTime, startTime, endTime, chartData, startTimeNeeded, linkId);
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("error = " + error);
                }
            });
            NetworkManager.getInstance(this).addToRequestQueue(jsonObjectRequest);
        }
    }

    private List<Long> getStartTimeByDay(Long startTime, Long endTime) {
        Long dayMillisFactor = Long.valueOf(24 * 3600 * 1000);
        ArrayList<Long> mTimeSteps = new ArrayList<>();
        mTimeSteps.add(startTime);
        Long nextDay = mTimeSteps.get(mTimeSteps.size() - 1) + dayMillisFactor;
        while (nextDay <= endTime) {
            mTimeSteps.add(nextDay);
            nextDay = mTimeSteps.get(mTimeSteps.size() - 1) + dayMillisFactor;
        }
        return mTimeSteps;
    }

    private boolean isNonFreeflow(JSONArray data) {
        Boolean ret = true;
        try {
            JSONObject obj = data.getJSONObject(0);
            ret = obj.has("coordinates");
        } catch (JSONException e) {
            return false;
        }
        return ret;
    }

    private Integer parseFreeflowData(JSONArray response) {
        List<Integer> chartData = new ArrayList<>();
        Integer ret = 0;
        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject obj = response.getJSONObject(i);
                Double data = obj.getJSONArray("freeFlowSpeed").getDouble(0);
                chartData.add(data.intValue());
                ret = data.intValue();
            } catch (JSONException e) {
                System.out.println("Invalid Json argument: " + e.toString());
            }
        }
        return ret;
    }

    private Integer parseNonFreeflowData(JSONArray response) {
        /*  Sample response element
            {
            "TravelTime": "236.0",
            "averageSpeed": "29.0",
            "coordinates": [
                [
                    -79.37224030456842,
                    43.85344147661279
                ],
                [
                    -79.37642030473204,
                    43.87038147614847
                ]
            ],
            "density": "100.0",
            "fid": "Obc78eb91f2f4711f20fe5e69e1f",
            "flow": 2776.65,
            "freeFlowSpeed": 95.75,
            "publicationTime_in_string": "2017-02-10T15:25:00Z",
            "publication_time": "2017-02-10 15:25:00",
            "timestamp": 1486740300.0
        }
         */
        List<Integer> chartData = new ArrayList<>();
        Integer ret = 0;
        try {
            JSONObject obj = response.getJSONObject(0);
            Integer data = obj.getInt("averageSpeed");
            chartData.add(data);
            ret = data;
        } catch (JSONException e) {
            System.out.println("Invalid Json argument: " + e.toString());
            ret = parseFreeflowData(response);
        }
        return ret;
    }

    public enum HistoricalChartTypes {
        AirQuality("Air Quality"),
        Speed("Speed");

        private final String chartType;

        HistoricalChartTypes(String type) {
            this.chartType = type;
        }
    }

    public enum HistoricalChartStatus {
        NEEDS_PERSIST, NEEDS_UPDATE, OKAY
    }

    public class HistoricalChartAdapter extends ArrayAdapter<HistoricalChartData> {

        public HistoricalChartAdapter(Context context, ArrayList<HistoricalChartData> objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            HistoricalChartData chartData = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.historical_chart_card, parent, false);
                convertView.setLongClickable(true);
            }
            TextView tvCaptions = (TextView) convertView.findViewById(
                    R.id.historical_dashboard_card_tv_captions);

            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm");
            Date startDate = new Date(chartData.mStartTime);
            Date endDate = new Date(chartData.mEndTime);
            tvCaptions.setText(chartData.mDataType + "\nfrom " + sdf.format(startDate) + " to " + sdf.format(endDate));
            createLineChart(convertView, chartData);
            return convertView;
        }
    }

    public class HistoricalChartData {
        HistoricalChartStatus mStatus;
        String mGraphId;
        String mChartType;
        String mDataType;
        List<Long> mTimeSteps;
        List<Integer> mData;
        String mDataTime; // HH:mm format

        Long mLastUpdatedTime;
        Long mStartTime;
        Long mEndTime;


        String mLinkId;
        String mAddress;

        HistoricalChartData(String chartType, String dataType, Long startTime, Long endTime,
                            String dataTime, List<Integer> data, List<Long> startTimeNeeded,
                            String graphId, HistoricalChartStatus status, String linkId) {
            mDataTime = dataTime;
            mDataType = dataType;
            mChartType = chartType;
            mStartTime = startTime;
            mEndTime = endTime;
            mData = data;
            mTimeSteps = startTimeNeeded;
            if (graphId == null) {
                mGraphId = Long.valueOf(System.currentTimeMillis()).toString();
            } else {
                mGraphId = graphId;
            }
            mLastUpdatedTime = System.currentTimeMillis();
            mStatus = status;
            mLinkId = linkId;
        }


        @Override
        public String toString() {
            return String.format(
                    "HistoricalChartData: dataType=%s, startTime=%s" +
                            "endTime=%s, dataTime=%s, data=%s",
                    mDataType, mStartTime.toString(), mEndTime.toString(), mDataTime, mData.toString());
        }
    }

    public class DateAxisFormatter implements IAxisValueFormatter {

        List<String> timeValues;

        public DateAxisFormatter(List<Long> inputTimes) {
            timeValues = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
            for (Long val : inputTimes) {
                String date = sdf.format(new Date(val));
                this.timeValues.add(date);
            }
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return ((int) value) < timeValues.size() ? timeValues.get((int) value) : "";
        }
    }


}

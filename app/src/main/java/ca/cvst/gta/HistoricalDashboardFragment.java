package ca.cvst.gta;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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

import static android.app.Activity.RESULT_OK;

public class HistoricalDashboardFragment extends Fragment {

    private static final int NEW_HISTORICAL_CHART_REQUEST = 1;
    private ArrayAdapter<HistoricalGraph> mHistoricalChartAdapter;
    private ArrayList<HistoricalGraph> mHistoricalChartList;
    private DbHelper mDbHelper;
    private Gson gson = new Gson();

    private OnFragmentInteractionListener mListener;

    public HistoricalDashboardFragment() {
        // Required empty public constructor
    }

    public static HistoricalDashboardFragment newInstance(String param1, String param2) {
        HistoricalDashboardFragment fragment = new HistoricalDashboardFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_historical_dashboard, container, false);

        Toolbar toolbar = (Toolbar) root.findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_historical_dashboard);
        mListener.setActionBar(toolbar);

        FloatingActionButton newChartBtn = (FloatingActionButton) root.findViewById(R.id.btn_new_historical_chart);
        newChartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), NewHistoricalChartActivity.class);

                startActivityForResult(intent, NEW_HISTORICAL_CHART_REQUEST);
            }
        });

        mHistoricalChartList = new ArrayList<>();
        mHistoricalChartAdapter = new HistoricalChartAdapter(getContext(), mHistoricalChartList);
        ListView listview = (ListView) root.findViewById(R.id.historical_dashboard_listview);
        listview.setAdapter(mHistoricalChartAdapter);
        registerForContextMenu(listview);

        // Init database
        mDbHelper = new DbHelper(getContext());
        List<HistoricalGraph> existingGraphs = loadAllGraphsFromDB();
        for (HistoricalGraph graph : existingGraphs) {
            if (graphNeedsUpdate(graph)) {
                updateDataFromCvst(graph);
            } else {
                mHistoricalChartAdapter.add(graph);
            }
        }

        return root;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Check which graphs needs to be saved
        for (int i = 0; i < mHistoricalChartAdapter.getCount(); i++) {
            HistoricalGraph graph = mHistoricalChartAdapter.getItem(i);
            HistoricalChartStatus status = graph.mStatus;
            switch (status) {
                case OKAY:
                    break;
                case NEEDS_PERSIST:
                    saveGraphToDB(graph);
                    break;
                case NEEDS_UPDATE:
                    updateGraphInDB(graph);
                    break;
                case NEEDS_DELETE:
                    deleteGraphFromDb(graph);
            }
        }

        // Save the graphs
        mDbHelper.close();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.historical_dashboard_listview) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle(mHistoricalChartList.get(info.position).mChartType);
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
                deleteGraphFromDb(mHistoricalChartList.get(info.position));
                mHistoricalChartAdapter.remove(mHistoricalChartList.get(info.position));
                mHistoricalChartAdapter.notifyDataSetChanged();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NEW_HISTORICAL_CHART_REQUEST) {
            if (resultCode == RESULT_OK) {
                String chartType = data.getStringExtra("CHART_NAME");
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

    public void createLineChart(View v, HistoricalGraph chartData) {
        List<Double> trafficData = chartData.mData;

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
            entries.add(new Entry((float) i,  trafficData.get(i).floatValue()));
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

    private List<HistoricalGraph> loadAllGraphsFromDB() {
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
        List<HistoricalGraph> ret = new ArrayList<>();
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
            List<Double> dataList = gson.fromJson(
                    cursor.getString(cursor.getColumnIndex(GraphContract.GraphEntry.DATA_LIST)),
                    new TypeToken<ArrayList<Double>>() {
                    }.getType());

            ret.add(new HistoricalGraph(chartType, dataType, startTime, endTime, dataTime,
                    dataList, timeSteps, graphId, HistoricalChartStatus.OKAY, linkId));
        }
        cursor.close();
        return ret;
    }

    private Boolean graphNeedsUpdate(HistoricalGraph graph) {
        LocalDateTime dateTime = new LocalDateTime(graph.mLastUpdatedTime);
        LocalDateTime updateTime = dateTime.plusDays(1);
        LocalDateTime currTime = new LocalDateTime();
        return currTime.isAfter(updateTime);
    }

    private void updateGraphInDB(HistoricalGraph graph) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

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

    private void deleteGraphFromDb(HistoricalGraph graph) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        // Define 'where' part of query.
        String selection = GraphContract.GraphEntry.GRAPH_ID + " LIKE ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { graph.mGraphId };
        // Issue SQL statement.
        db.delete(GraphContract.GraphEntry.TABLE_NAME, selection, selectionArgs);
    }

    private void saveGraphToDB(HistoricalGraph graph) {
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

    private void updateDataFromCvst(final HistoricalGraph graph) {
        Long timeDelta = graph.mEndTime - graph.mStartTime;
        Long newEndTime = System.currentTimeMillis();
        Long newStartTime = newEndTime - timeDelta;

        final List<Long> startTimeNeeded = getStartTimeByDay(newStartTime, newEndTime);
        final List<Double> chartData = new ArrayList<>();

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
                                chartData.add(parseNonFreeflowData(response, graph.mDataType));
                            } else {
                                chartData.add(parseFreeflowData(response, graph.mDataType));
                            }
                            System.out.println("DATA RECEIVED!!" + chartData.toString());
                            // Create new graph
                            if (chartData.size() == startTimeNeeded.size()) {
                                graph.mData = chartData;
                                graph.mTimeSteps = startTimeNeeded;
                                mHistoricalChartAdapter.add(graph);
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("historical dashboard error = " + error);
                }
            });
            NetworkManager.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
        }

    }

    private void fetchDataFromCvst(final String linkId, final String dataType, final String chartType,
                                   final String chartDataTime, final Long startTime, final Long endTime) {
        final List<Long> startTimeNeeded = getStartTimeByDay(startTime, endTime);
        final List<Double> chartData = new ArrayList<>();

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
                                chartData.add(parseNonFreeflowData(response, dataType));
                            } else {
                                chartData.add(parseNonFreeflowData(response, dataType));
                            }
                            System.out.println("DATA RECEIVED!!" + chartData.toString());
                            // Create new graph
                            if (chartData.size() == startTimeNeeded.size()) {
                                createHistoricalChart(chartType, dataType, chartDataTime, startTime, endTime, chartData, startTimeNeeded, linkId);
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("error = " + error);
                }
            });
            NetworkManager.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
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

    private Double parseFreeflowData(JSONArray response, String dataType) {
        /*
         {
            "density": [
                0.9267567099456242
            ],
            "fid": [
                "Obc775a71f1592229917fb6a34a1d"
            ],
            "flow": [
                74.14053679564994
            ],
            "freeFlowSpeed": [
                "80.0"
            ],
            "freeFlowtraveltime": [
                "62.46"
            ]
        }
         */

        try {
            JSONObject obj = response.getJSONObject(0);

            if (dataType.equals("Average Speed")) {
                return obj.getJSONArray("freeFlowSpeed").getDouble(0);
            } else if (dataType.equals("Travel Time")){
                return obj.getJSONArray("freeFlowtraveltime").getDouble(0);
            } else if (dataType.equals("Flow")){
                return obj.getJSONArray("flow").getDouble(0);
            } else if (dataType.equals("Density")){
                return obj.getJSONArray("density").getDouble(0);
            }
        } catch (JSONException e) {
            System.out.println("Invalid Json argument: " + e.toString());
        }
        return 0.0;
    }

    private Double parseNonFreeflowData(JSONArray response, String dataType) {
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
        try {
            JSONObject obj = response.getJSONObject(0);
            if (dataType.equals("Average Speed")) {
                return obj.getDouble("averageSpeed");
            } else if (dataType.equals("Travel Time")){
                return obj.getDouble("TravelTime");
            } else if (dataType.equals("Flow")){
                return obj.getDouble("flow");
            } else if (dataType.equals("Density")){
                return obj.getDouble("density");
            }
        } catch (JSONException e) {
            System.out.println("Invalid Json argument: " + e.toString());
            return parseFreeflowData(response, dataType);
        }
        return 0.0;
    }

    private void createHistoricalChart(String chartType, String dataType, String chartDataTime, Long startTime,
                                       Long endTime, List<Double> chartData, List<Long> startTimeNeeded, String linkId) {
        mHistoricalChartAdapter.add(new HistoricalGraph(
                chartType, dataType, startTime, endTime, chartDataTime, chartData,
                startTimeNeeded, null, HistoricalChartStatus.NEEDS_PERSIST, linkId
        ));
    }


    public enum HistoricalChartStatus {
        NEEDS_PERSIST, NEEDS_UPDATE, NEEDS_DELETE, OKAY
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void setActionBar(Toolbar toolbar);
    }

    public class HistoricalChartAdapter extends ArrayAdapter<HistoricalGraph> {

        public HistoricalChartAdapter(Context context, ArrayList<HistoricalGraph> objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            HistoricalGraph chartData = getItem(position);
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
            tvCaptions.setText(chartData.mChartType + "\nfrom " + sdf.format(startDate) + " to " + sdf.format(endDate));
            createLineChart(convertView, chartData);
            return convertView;
        }
    }

    public class HistoricalGraph {
        HistoricalChartStatus mStatus;
        String mGraphId;
        String mChartType;
        String mDataType;
        List<Long> mTimeSteps;
        List<Double> mData;
        String mDataTime; // HH:mm format

        Long mLastUpdatedTime;
        Long mStartTime;
        Long mEndTime;


        String mLinkId;
        String mAddress;

        HistoricalGraph(String chartType, String dataType, Long startTime, Long endTime,
                        String dataTime, List<Double> data, List<Long> startTimeNeeded,
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
                    "HistoricalGraph: dataType=%s, startTime=%s" +
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

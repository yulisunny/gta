package ca.cvst.gta;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HistoricalDashboardActivity extends AppCompatActivity {

    private static final int NEW_HISTORICAL_CHART_REQUEST = 1;

    private ArrayAdapter<HistoricalChartData> historicalChartAdapter;
    private Integer currChartCompletedDays = 0;

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

        ArrayList<HistoricalChartData> list = new ArrayList<>();
        historicalChartAdapter = new HistoricalChartAdapter(this, list);
        ListView listview = (ListView) findViewById(R.id.historical_dashboard_listview);
        listview.setAdapter(historicalChartAdapter);
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
                                       Long endTime, List<Integer> chartData, List<Long> startTimeNeeded)
    {
        historicalChartAdapter.add(new HistoricalChartData(
                chartType, chartType, startTime ,endTime ,chartDataTime, chartData, startTimeNeeded
        ));
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
            }
            TextView tvCaptions = (TextView) convertView.findViewById(
                    R.id.historical_dashboard_card_tv_captions);

            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm");
            Date startDate = new Date(chartData.mStartTime);
            Date endDate = new Date(chartData.mEndTime);
            tvCaptions.setText(chartData.mDataType  + "\nfrom " + sdf.format(startDate) + " to " + sdf.format(endDate));
            createLineChart(convertView, chartData);
            return convertView;
        }
    }


    public void createLineChart(View v, HistoricalChartData chartData) {
        List<Integer> trafficData = chartData.mData;

        LineChart chart = (LineChart) v.findViewById(R.id.historical_dashboard_card_iv);

        // Set Axis
        XAxis xAxis = chart.getXAxis();
        System.out.println("timesteps are " + chartData.mTimeSteps.toString());
        xAxis.setValueFormatter(new DateAxisFormatter(chartData.mTimeSteps));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawLabels(true);

        YAxis yRightAxis = chart.getAxisRight();
        yRightAxis.setEnabled(false);

        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < trafficData.size(); i ++) {
            entries.add(new Entry((float)i , (float)trafficData.get(i)));
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

    private void fetchDataFromCvst(String linkId, final String dataType, final String chartType,
                                   final String chartDataTime, final Long startTime, final Long endTime)
    {
        final List<Long> startTimeNeeded = getStartTimeByDay(startTime, endTime);
        final List<Integer> chartData = new ArrayList<>();

        for (Long startDay:startTimeNeeded) {
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
                                createHistoricalChart(chartType, chartDataTime, startTime, endTime, chartData, startTimeNeeded);
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

    private List<Long> getStartTimeByDay(Long startTime, Long endTime){
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

    public class HistoricalChartData {

        String mChartType;
        String mDataType;
        List<Long> mTimeSteps;
        List<Integer> mData;
        Long mStartTime;
        Long mEndTime;
        LocalTime mDataTime;
        String mLinkId;
        String mAddress;

        HistoricalChartData(String chartType, String dataType, Long startTime, Long endTime,
                                   String dataTime, List<Integer> data, List<Long> startTimeNeeded) {
            DateTimeFormatter formatter = DateTimeFormat.forPattern("HH:mm");
            mDataTime = formatter.parseLocalTime(dataTime);
            mDataType = dataType;
            mChartType = chartType;
            mStartTime = startTime;
            mEndTime = endTime;
            mData = data;
            mTimeSteps = startTimeNeeded;
        }


        @Override
        public String toString() {
            return String.format(
                    "HistoricalChartData: dataType=%s, startTime=%s" +
                    "endTime=%s, dataTime=%s, data=%s",
                     mDataType, mStartTime.toString(), mEndTime.toString(),
                    DateTimeFormat.forPattern("HH:mm").print(mDataTime), mData.toString());
        }
    }

    public class DateAxisFormatter implements IAxisValueFormatter {

        List<String> timeValues;

        public DateAxisFormatter(List<Long> inputTimes) {
            timeValues = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
            for(Long val:inputTimes) {
                String date = sdf.format(new Date(val));
                this.timeValues.add(date);
            }
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return timeValues.get((int) value);
        }
    }



}

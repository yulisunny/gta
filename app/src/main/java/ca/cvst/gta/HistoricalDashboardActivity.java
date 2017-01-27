package ca.cvst.gta;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class HistoricalDashboardActivity extends AppCompatActivity {

    private static final int NEW_HISTORICAL_CHART_REQUEST = 1;

    private ArrayAdapter<HistoricalChartData> historicalChartAdapter;

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



                historicalChartAdapter.add(new HistoricalChartData(
                        chartType, chartType, startTime ,endTime ,chartDataTime, new JSONArray()
                ));
            }
        }
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
            tvCaptions.setText(String.format("%d, %s", position, chartData.toString()));
            createLineChart(convertView);
            return convertView;
        }
    }


    public void createLineChart(View v) {
        List<Integer> fakeData = new ArrayList<>();
        Random ran = new Random();
        for (int i = 0; i < 10; i ++) {
            fakeData.add(ran.nextInt(10));
        }

        LineChart chart = (LineChart) v.findViewById(R.id.historical_dashboard_card_iv);
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < fakeData.size(); i ++) {
            entries.add(new Entry((float)i , (float)fakeData.get(i)));
        }
        LineDataSet dataSet = new LineDataSet(entries, "Highway Flow Speed");
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate();
    }

    public class HistoricalChartData {

        private String mChartType;
        private String mDataType;
        private JSONArray mData;
        private Long mStartTime;
        private Long mEndTime;
        private LocalTime mDataTime;

        public HistoricalChartData(String chartType, String dataType, Long startTime, Long endTime,
                                   String dataTime, JSONArray data) {
            DateTimeFormatter formatter = DateTimeFormat.forPattern("HH:mm");
            mDataTime = formatter.parseLocalTime(dataTime);
            mDataType = dataType;
            mChartType = chartType;
            mStartTime = startTime;
            mEndTime = endTime;
            mData = data;
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

}

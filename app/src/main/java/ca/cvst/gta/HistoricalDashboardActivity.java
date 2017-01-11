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

import java.util.ArrayList;
import java.util.HashMap;

public class HistoricalDashboardActivity extends AppCompatActivity {

    private static final int NEW_HISTORICAL_CHART_REQUEST = 1;

    private ArrayAdapter<String> historicalChartAdapter;

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

//        String[] values = new String[] { "Android", "iPhone", "WindowsMobile",
//                "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
//                "Linux", "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux",
//                "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux", "OS/2",
//                "Android", "iPhone", "WindowsMobile" };

        ArrayList<String> list = new ArrayList<>();
//        for (int i = 0; i < values.length; ++i) {
//            list.add(values[i]);
//        }
        historicalChartAdapter = new HistoricalChartAdapter(this, list);
        ListView listview = (ListView) findViewById(R.id.historical_dashboard_listview);
        listview.setAdapter(historicalChartAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NEW_HISTORICAL_CHART_REQUEST) {
            if (resultCode == RESULT_OK) {
                String msg = data.getStringExtra("MESSAGE");
                historicalChartAdapter.add(msg);
            }
        }
    }

//    private JSONObject getHistoricalDataWithTimeRange(
//            HashMap<String, String> params, Long startTimeEpochMs, Long endTimeEpochMs) throws Exception {
//        String temp = "{test: data}";
//        return new JSONObject(temp);
//    }

    public class HistoricalChartAdapter extends ArrayAdapter<String> {

        public HistoricalChartAdapter(Context context, ArrayList<String> objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            String object = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.historical_chart_card, parent, false);
            }

            TextView tvCaptions = (TextView) convertView.findViewById(R.id.historical_dashboard_card_tv_captions);
            tvCaptions.setText(object);
            return convertView;
        }
    }

}

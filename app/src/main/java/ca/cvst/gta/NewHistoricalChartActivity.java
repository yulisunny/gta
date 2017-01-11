package ca.cvst.gta;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

public class NewHistoricalChartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_historical_chart);

//        Spinner spinnerTrafficType = (Spinner) findViewById(R.id.spinner_new_historical_chart_type);
//        Spinner spinnerTimeRange = (Spinner) findViewById(R.id.spinner_new_historical_chart_time_range);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button completeFormBtn = (Button) findViewById(R.id.btn_add_historical_chart);
        completeFormBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message =  "NEW CHART WOOT";
                Intent intent = new Intent();
                intent.putExtra("MESSAGE", message);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

}

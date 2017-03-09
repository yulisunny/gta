package ca.cvst.gta;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.format.DateFormat;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;

import java.util.Calendar;
import java.util.Date;

public class NewHistoricalChartActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    private static final int NEW_HISTORICAL_CHART_MAP_HIGHWAY_REQUEST = 2;
    private static final int NEW_HISTORICAL_CHART_MAP_AIR_QUALITY_REQUEST = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_historical_chart);

        final Spinner spinnerTrafficType = (Spinner) findViewById(R.id.spinner_new_historical_chart_type);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Disable keyboard pop up
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Picking a time to query historical data
        final EditText dataTime = (EditText) findViewById(R.id.edit_new_historical_chart_data_time);
        LocalTime currTime = new LocalTime();
        dataTime.setText(DateTimeFormat.forPattern("HH:mm").print(currTime)); //default time
        dataTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        // Complete Form
        final Button completeFormBtn = (Button) findViewById(R.id.btn_add_historical_chart);
        completeFormBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent linkSelectionIntent = new Intent(getApplicationContext(), NewHistoricalMapActivity.class);
            if (spinnerTrafficType.getSelectedItemPosition() == 0) {
                // Select an air quality device
                linkSelectionIntent.putExtra("TYPE", "AIR_QUALITY");
                startActivityForResult(linkSelectionIntent, NEW_HISTORICAL_CHART_MAP_AIR_QUALITY_REQUEST);
            } else {
                // Select a link for highway speed
                linkSelectionIntent.putExtra("TYPE", "HIGHWAY");
                startActivityForResult(linkSelectionIntent, NEW_HISTORICAL_CHART_MAP_HIGHWAY_REQUEST);
            }
            }
        });

        // Showing and hiding parts of the form depending on traffic type
        final View hwView = findViewById(R.id.layout_chart_highway);
        hwView.setVisibility(View.GONE);
        final View airQualityView = findViewById(R.id.layout_chart_air_quality);
        airQualityView.setVisibility(View.GONE);

        spinnerTrafficType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = spinnerTrafficType.getSelectedItem().toString();
                if (item.equals("Road Traffic")) {
                    hwView.setVisibility(View.VISIBLE);
                    airQualityView.setVisibility(View.GONE);
                    completeFormBtn.setText(R.string.add_new_historical_chart_button_next);
                } else {
                    hwView.setVisibility(View.GONE);
                    airQualityView.setVisibility(View.VISIBLE);
                    completeFormBtn.setText(R.string.add_new_historical_chart_button_next);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @NonNull
    private Intent createResultIntent() {
        final Spinner spinnerTrafficType = (Spinner) findViewById(R.id.spinner_new_historical_chart_type);
        final Spinner spinnerTimeRange = (Spinner) findViewById(R.id.spinner_new_historical_chart_time_range);
        final EditText dataTime = (EditText) findViewById(R.id.edit_new_historical_chart_data_time);
        final EditText chartNameEdit = (EditText) findViewById(R.id.edit_new_historical_chart_name);

        String chartType = spinnerTrafficType.getSelectedItem().toString();
        String chartDataTime = dataTime.getText().toString();
        String chartName = chartNameEdit.getText().toString();
        Date refTime = getCurrTime(chartDataTime);

        Intent intent = new Intent();
        intent.putExtra("CHART_NAME", chartName);
        intent.putExtra("CHART_TYPE", chartType);
        intent.putExtra("DATA_TIME", chartDataTime);
        intent.putExtra("END_TIME", refTime.getTime());
        intent.putExtra("START_TIME",getTimestamp(spinnerTimeRange.getSelectedItem().toString(), refTime));
        return intent;
    }

    private Long getTimestamp(String timeSelection, Date referenceTime) {
        Date currDate = referenceTime;
        Long dayMillisFactor = Long.valueOf(24 * 3600 * 1000);
        if (timeSelection.equals("Three Days")) {
            return new Date(currDate.getTime() - 3 * dayMillisFactor).getTime();
        } else if (timeSelection.equals("One Week")) {
            return new Date(currDate.getTime() - 7 * dayMillisFactor).getTime();
        } else if (timeSelection.equals("One Month")) {
            return new Date(currDate.getTime() - 30 * dayMillisFactor).getTime();
        }
        return currDate.getTime();
    }

    private Date getCurrTime(String chartDataTime) {
        String[] time =  chartDataTime.split(":");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(time[0]));
        calendar.set(Calendar.MINUTE, Integer.valueOf(time[1]));
        return calendar.getTime();
    }


    @Override
    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
        EditText dataTime = (EditText) findViewById(R.id.edit_new_historical_chart_data_time);
        LocalTime time = new LocalTime(selectedHour, selectedMinute);
        dataTime.setText(DateTimeFormat.forPattern("HH:mm").print(time));
        dataTime.setInputType(InputType.TYPE_NULL);
    }

    public void showTimePickerDialog() {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }


    public static class TimePickerFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            TimePickerDialog.OnTimeSetListener listener = (TimePickerDialog.OnTimeSetListener) getActivity();
            TimePickerDialog dialog =  new TimePickerDialog(getActivity(), listener, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
            dialog.setTitle("Select a time");
            return dialog;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NEW_HISTORICAL_CHART_MAP_HIGHWAY_REQUEST) {
            if (resultCode == RESULT_OK) {
                Intent intent = createResultIntent();
                String linkId = data.getStringExtra("LINK_ID");
                Spinner hwDataType = (Spinner) findViewById(R.id.spinner_new_historical_chart_highway_type);
                intent.putExtra("LINK_ID", linkId);
                intent.putExtra("DATA_TYPE_POS", hwDataType.getSelectedItemPosition());
                setResult(RESULT_OK, intent);
                finish();
            }
        } else if (requestCode == NEW_HISTORICAL_CHART_MAP_AIR_QUALITY_REQUEST) {
            if (resultCode == RESULT_OK) {
                Intent intent = createResultIntent();
                String linkId = data.getStringExtra("DEVICE_ID");
                Spinner aqDataType = (Spinner) findViewById(R.id.spinner_new_historical_chart_air_quality_data_types);
                intent.putExtra("DEVICE_ID", linkId);
                intent.putExtra("DATA_TYPE_POS", aqDataType.getSelectedItemPosition());
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }

}

package ca.cvst.gta;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class NewHistoricalChartActivity extends AppCompatActivity {

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
        LocalTime currTime = new LocalTime();
        Date date = new Date();
        final EditText startTime = (EditText) findViewById(R.id.edit_new_historical_chart_start_time);
//        startTime.setText(DateTimeFormat.forPattern("HH:mm").print(currTime)); //default time
        startTime.setOnClickListener(getTimePickOnClickListener(startTime));
        startTime.setRawInputType(InputType.TYPE_NULL);
        startTime.setFocusable(false);

        final EditText endTime = (EditText) findViewById(R.id.edit_new_historical_chart_end_time);
//        endTime.setText(DateTimeFormat.forPattern("HH:mm").print(currTime)); //default time
        endTime.setOnClickListener(getTimePickOnClickListener(endTime));
        endTime.setRawInputType(InputType.TYPE_NULL);
        endTime.setFocusable(false);

        final EditText startDate = (EditText)findViewById(R.id.edit_new_historical_chart_start_date);
//        startDate.setText(DateTimeFormat.forPattern("MM/dd/yyyy").print(date.getTime())); //default time
        startDate.setOnClickListener(getDatePickOnClickListener(startDate));
        startDate.setRawInputType(InputType.TYPE_NULL);
        startDate.setFocusable(false);

        final EditText endDate = (EditText)findViewById(R.id.edit_new_historical_chart_end_date);
//        endDate.setText(DateTimeFormat.forPattern("MM/dd/yyyy").print(date.getTime())); //default time
        endDate.setOnClickListener(getDatePickOnClickListener(endDate));
        endDate.setRawInputType(InputType.TYPE_NULL);
        endDate.setFocusable(false);

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
    private View.OnClickListener getTimePickOnClickListener(final EditText editText) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog.OnTimeSetListener tpd = new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        LocalTime time = new LocalTime(selectedHour, selectedMinute);
                        editText.setText(DateTimeFormat.forPattern("HH:mm").print(time));
                        editText.setInputType(InputType.TYPE_NULL);
                    }
                };

                // Use the current time as the default values for the picker
                final Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                // Create a new instance of TimePickerDialog and return it
                new TimePickerDialog(NewHistoricalChartActivity.this, tpd, hour, minute, false).show();
            }
        };
    }

    @NonNull
    private View.OnClickListener getDatePickOnClickListener(final EditText editText) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog.OnDateSetListener dpd = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar myCalendar = Calendar.getInstance();
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        editText.setText(new SimpleDateFormat("MM/dd/yyyy").format(myCalendar.getTime()));
                    }
                };

                // Use the current date as the default date in the picker
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog d = new DatePickerDialog(NewHistoricalChartActivity.this, dpd, year , month, day);
                d.show();
            }
        };
    }

    private Intent createResultIntentForSpecificTimeRange() {
        final Spinner spinnerTrafficType = (Spinner) findViewById(R.id.spinner_new_historical_chart_type);
        final EditText chartNameEdit = (EditText) findViewById(R.id.edit_new_historical_chart_name);
        final EditText startTime = (EditText) findViewById(R.id.edit_new_historical_chart_start_time);
        final EditText startDate = (EditText) findViewById(R.id.edit_new_historical_chart_start_date);
        final EditText endTime = (EditText) findViewById(R.id.edit_new_historical_chart_end_time);
        final EditText endDate = (EditText) findViewById(R.id.edit_new_historical_chart_end_date);

        String chartType = spinnerTrafficType.getSelectedItem().toString();
        String chartName = chartNameEdit.getText().toString();

        Intent intent = new Intent();
        intent.putExtra("CHART_NAME", chartName);
        intent.putExtra("CHART_TYPE", chartType);
        intent.putExtra("DATA_TIME", getTimeDiff(startTime.getText().toString(), endTime.getText().toString()));
        intent.putExtra("END_TIME", getEpochMs(endTime.getText().toString(), endDate.getText().toString()));
        intent.putExtra("START_TIME", getEpochMs(startTime.getText().toString(), startDate.getText().toString()));
        return intent;
    }

    private Intent createResultIntentForRelativeTimeRange() {
        final Spinner spinnerTrafficType = (Spinner) findViewById(R.id.spinner_new_historical_chart_type);
        final Spinner spinnerTimeRange = (Spinner) findViewById(R.id.spinner_new_historical_chart_time_range);
        final EditText dataTime = (EditText) findViewById(R.id.edit_new_historical_chart_start_time);
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

    private Long getTimeDiff(String startTime, String endTime) {
        System.out.println("GETTING TIME DIFFS " + startTime + " " + endTime);
        Date startDate = getCurrTime(startTime);
        Date endDate = getCurrTime(endTime);
        return endDate.getTime() - startDate.getTime();
    }

    private Date getCurrTime(String chartDataTime) {
        String[] time =  chartDataTime.split(":");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(time[0]));
        calendar.set(Calendar.MINUTE, Integer.valueOf(time[1]));
        return calendar.getTime();
    }

    private Long getEpochMs(String inputTime, String inputDate) {
        String[] time =  inputTime.split(":");
        String[] dateParams = inputDate.split("/");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(time[0]));
        calendar.set(Calendar.MINUTE, Integer.valueOf(time[1]));
        calendar.set(Calendar.MONTH, Integer.valueOf(dateParams[0]));
        calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(dateParams[1]));
        calendar.set(Calendar.YEAR, Integer.valueOf(dateParams[2]));
        calendar.setTimeZone(TimeZone.getTimeZone("EDT"));
        return calendar.getTime().getTime();
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
                Intent intent = createResultIntentForSpecificTimeRange();
                String linkId = data.getStringExtra("LINK_ID");
                Spinner hwDataType = (Spinner) findViewById(R.id.spinner_new_historical_chart_highway_type);
                intent.putExtra("LINK_ID", linkId);
                intent.putExtra("DATA_TYPE_POS", hwDataType.getSelectedItemPosition());
                setResult(RESULT_OK, intent);
                finish();
            }
        } else if (requestCode == NEW_HISTORICAL_CHART_MAP_AIR_QUALITY_REQUEST) {
            if (resultCode == RESULT_OK) {
                Intent intent = createResultIntentForSpecificTimeRange();
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

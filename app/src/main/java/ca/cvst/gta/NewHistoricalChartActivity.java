package ca.cvst.gta;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;

import java.util.Calendar;
import java.util.Date;

public class NewHistoricalChartActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    private static final int NEW_HISTORICAL_CHART_MAP_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_historical_chart);

        final Spinner spinnerTrafficType = (Spinner) findViewById(R.id.spinner_new_historical_chart_type);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Disable keyboard pop up
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Showing and hiding parts of the form depending on traffic type
        final View hwView = findViewById(R.id.layout_chart_highway);
        hwView.setVisibility(View.GONE);
        spinnerTrafficType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = spinnerTrafficType.getSelectedItem().toString();
                if (item.equals("Free flow speed of Roads")) {
                    hwView.setVisibility(View.VISIBLE);
                } else {
                    hwView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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
        Button completeFormBtn = (Button) findViewById(R.id.btn_add_historical_chart);
        completeFormBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Select a link
                Intent linkSelectionIntent = new Intent(getApplicationContext(), NewHistoricalChartMapActivity.class);
                startActivityForResult(linkSelectionIntent, NEW_HISTORICAL_CHART_MAP_REQUEST);
            }
        });

    }

    private Long getTimestamp(String timeSelection) {
        Date currDate = new Date();
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


    @Override
    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
        EditText dataTime = (EditText) findViewById(R.id.edit_new_historical_chart_data_time);
        LocalTime time = new LocalTime(selectedHour, selectedMinute);
        dataTime.setText(DateTimeFormat.forPattern("HH:mm").print(time));
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
        if (requestCode == NEW_HISTORICAL_CHART_MAP_REQUEST) {
            if (resultCode == RESULT_OK) {
                String linkId = data.getStringExtra("LINK_ID");

                final Spinner spinnerTrafficType = (Spinner) findViewById(R.id.spinner_new_historical_chart_type);
                final Spinner spinnerTimeRange = (Spinner) findViewById(R.id.spinner_new_historical_chart_time_range);
                final EditText dataTime = (EditText) findViewById(R.id.edit_new_historical_chart_data_time);

                String chartType = spinnerTrafficType.getSelectedItem().toString();
                String chartDataTime = dataTime.getText().toString();
                Intent intent = new Intent();
                intent.putExtra("CHART_TYPE", chartType);
                intent.putExtra("DATA_TIME", chartDataTime);
                intent.putExtra("START_TIME", new Date().getTime());
                intent.putExtra("END_TIME",getTimestamp(spinnerTimeRange.getSelectedItem().toString()));
                intent.putExtra("LINK_ID", linkId);
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }




//    private void initLinkIds() {
//        String url = "http://portal.cvst.ca/api/0.1/tomtom/hdf/linkids";
//        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url,
//                new Response.Listener<JSONArray>() {
//                    @Override
//                    public void onResponse(JSONArray linkIds) {
//                        System.out.println(linkIds.length());
//                        if (linkIds.length() > 0) {
//                            for (int i = 0; i < linkIds.length(); i++) {
//                                try {
//                                    String linkId = linkIds.getString(i);
//                                    addLinkIdToAddressMapping(linkId);
//                                    System.out.println(linkIdAddressMapping.toString());
//                                } catch (JSONException e) {
//                                    System.out.println(e);
//                                }
//                            }
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                System.out.println("error = " + error);
//            }
//        });
//
//        NetworkManager.getInstance(this).addToRequestQueue(jsonArrayRequest);
//    }
//
//    private void addLinkIdToAddressMapping(final String linkId) {
//        String url = String.format("http://portal.cvst.ca/api/0.1/tomtom/hdf/linkLocations/analytics/%s", linkId);
//
//        RequestFuture<JSONObject> future = RequestFuture.newFuture();
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, future, future);
//        NetworkManager.getInstance(this).addToRequestQueue(request);
//
//        try {
//            JSONObject response = future.get();
//            String address = response.get("From").toString();
//            linkIdAddressMapping.put(linkId, address);
//        } catch (JSONException e) {
//            System.out.println("Invalid Json argument: " + e.toString());
//        } catch (InterruptedException e) {
//            System.out.println("Received Interrupted Exception: " + e.toString());
//        } catch (ExecutionException e) {
//            System.out.println("Received Execution Exception: " + e.toString());
//        }

        // Async
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        /*
//                        {
//                            "From": "ON-400, North York, ON M9N 2V9, Canada",
//                            "To": 0,
//                            "elaboratedDataID": "Obc775a71f1592229917fb6a34a1d"
//                         }
//                         */
//                        try {
//                            String address = response.get("From").toString();
//                            linkIdAddressMapping.put(linkId, address);
//                        } catch (JSONException e) {
//                            System.out.println("Invalid Json argument: " + e.toString());
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                System.out.println("error = " + error);
//            }
//        });
//        NetworkManager.getInstance(this).addToRequestQueue(jsonObjectRequest);
//    }




}

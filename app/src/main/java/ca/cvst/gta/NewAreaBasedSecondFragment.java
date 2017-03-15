package ca.cvst.gta;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import java.util.Calendar;

public class NewAreaBasedSecondFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private OnFragmentInteractionListener mListener;
    private static View root;
    private ArrayAdapter<CharSequence> mPublishersAdapter;
    private Spinner mPublishersSpinner;
    private String publisher = null;
    private int[] mondayToSundayArray;
    private int isNotificationEnabled;

    public NewAreaBasedSecondFragment() {
        // Rquried empty public constructor
    }

    public static NewAreaBasedSecondFragment newInstance() {
        NewAreaBasedSecondFragment fragment = new NewAreaBasedSecondFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_area_based_subscriptions_second_page, container, false);

        mondayToSundayArray = new int[] {0,0,0,0,0,0,0};
        isNotificationEnabled = 0;

        ToggleButton toggleSunday = (ToggleButton) root.findViewById(R.id.Sunday);
        toggleSunday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mondayToSundayArray[6] = 1;
                    // toggle enabled
                } else {
                    mondayToSundayArray[6] = 0;
                    // toggle disabled
                }
            }
        });

        ToggleButton toggleMonday = (ToggleButton) root.findViewById(R.id.Monday);
        toggleMonday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mondayToSundayArray[0] = 1;
                    // toggle enabled
                } else {
                    mondayToSundayArray[0] = 0;
                    // toggle disabled
                }
            }
        });

        ToggleButton toggleTuesday = (ToggleButton) root.findViewById(R.id.Tuesday);
        toggleTuesday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mondayToSundayArray[1] = 1;
                    // toggle enabled
                } else {
                    mondayToSundayArray[1] = 0;
                    // toggle disabled
                }
            }
        });

        ToggleButton toggleWednesday = (ToggleButton) root.findViewById(R.id.Wednesday);
        toggleWednesday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mondayToSundayArray[2] = 1;
                    // toggle enabled
                } else {
                    mondayToSundayArray[2] = 0;
                    // toggle disabled
                }
            }
        });

        ToggleButton toggleThursday = (ToggleButton) root.findViewById(R.id.Thursday);
        toggleThursday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mondayToSundayArray[3] = 1;
                    // toggle enabled
                } else {
                    mondayToSundayArray[3] = 0;
                    // toggle disabled
                }
            }
        });

        ToggleButton toggleFriday = (ToggleButton) root.findViewById(R.id.Friday);
        toggleFriday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mondayToSundayArray[4] = 1;
                    // toggle enabled
                } else {
                    mondayToSundayArray[4] = 0;
                    // toggle disabled
                }
            }
        });

        ToggleButton toggleSaturday = (ToggleButton) root.findViewById(R.id.Saturday);
        toggleSaturday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mondayToSundayArray[5] = 1;
                    // toggle enabled
                } else {
                    mondayToSundayArray[5] = 0;
                    // toggle disabled
                }
            }
        });

        final Switch notificationEnabled = (Switch) root.findViewById(R.id.notification_enabled);
        notificationEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isNotificationEnabled = 1;
                } else {
                    isNotificationEnabled = 0;
                }
            }
        });

//        mPublishersSpinner = (Spinner) root.findViewById(R.id.spinner_publisher);
//        mPublishersAdapter = ArrayAdapter.createFromResource(root.getContext(), R.array.publishers_array, android.R.layout.simple_spinner_item);
//        mPublishersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        mPublishersSpinner.setAdapter(mPublishersAdapter);
//        mPublishersSpinner.setOnItemSelectedListener(this);

        EditText startTime = (EditText) root.findViewById(R.id.startTime);
//        LocalTime currTime = new LocalTime();
//        startTime.setText(DateTimeFormat.forPattern("HH:mm").print(currTime)); //default time

        startTime.setText("00:00");
        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialogStartTime();
            }
        });

        EditText endTime = (EditText) root.findViewById(R.id.endTime);
//        endTime.setText(DateTimeFormat.forPattern("HH:mm").print(currTime)); //default time
        endTime.setText("23:59");
        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialogEndTime();
            }
        });

        Button nextButton = (Button) root.findViewById(R.id.btn_next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (publisher != null) {
//                    mListener.setPublisher(mPublishersSpinner.getSelectedItem().toString());
                    mListener.setMondayToSunday(mondayToSundayArray);
                    mListener.setNotificationEnabled(isNotificationEnabled);
                    EditText startTime = (EditText) root.findViewById(R.id.startTime);
                    EditText endTime = (EditText) root.findViewById(R.id.endTime);
                    int startTimeInSec = convertToSeconds(startTime.getText().toString());
                    int endTimeInSec = convertToSeconds(endTime.getText().toString());
                    int[] startAndEndTime = new int[] {startTimeInSec, endTimeInSec};
                    mListener.setStartAndEndTime(startAndEndTime);
                    mListener.goToThirdSubscriptionPageFromSecondPage();
                    //mListener.submitSubscription();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Please select an area on the map", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button previousButton = (Button) root.findViewById(R.id.btn_previous);
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.goToFirstSubscriptionPageFromSecondPage();
            }
        });

        return root;
        //return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NewAreaBasedSecondFragment.OnFragmentInteractionListener) {
            mListener = (NewAreaBasedSecondFragment.OnFragmentInteractionListener) context;
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
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        if (parent.getId() == R.id.spinner_publisher) {
            publisher = mPublishersSpinner.getSelectedItem().toString();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    public void showTimePickerDialogStartTime() {
        TimePickerFragmentStartTime newFragment = new TimePickerFragmentStartTime();
        newFragment.setTargetFragment(this, 0);
        newFragment.show(this.getFragmentManager(), "timePicker");
    }

    public void showTimePickerDialogEndTime() {
        TimePickerFragmentEndTime newFragment = new TimePickerFragmentEndTime();
        newFragment.setTargetFragment(this, 0);
        newFragment.show(this.getFragmentManager(), "timePicker");
    }

    public static class TimePickerFragmentStartTime extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
//            final Calendar c = Calendar.getInstance();
//            int hour = c.get(Calendar.HOUR_OF_DAY);
//            int minute = c.get(Calendar.MINUTE);
            int hour = 0;
            int minute = 0;
            TimePickerDialog dialog =  new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
            dialog.setTitle("Select a time");
            return dialog;
        }

        @Override
        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

            EditText dataTime = (EditText) root.findViewById(R.id.startTime);
            LocalTime time = new LocalTime(selectedHour, selectedMinute);
            dataTime.setText(DateTimeFormat.forPattern("HH:mm").print(time));
            dataTime.setInputType(InputType.TYPE_NULL);
        }
    }

    public static class TimePickerFragmentEndTime extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
//            final Calendar c = Calendar.getInstance();
//            int hour = c.get(Calendar.HOUR_OF_DAY);
//            int minute = c.get(Calendar.MINUTE);
            int hour = 23;
            int minute = 59;
            TimePickerDialog dialog =  new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
            dialog.setTitle("Select a time");
            return dialog;
        }

        @Override
        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

            EditText dataTime = (EditText) root.findViewById(R.id.endTime);
            LocalTime time = new LocalTime(selectedHour, selectedMinute);
            dataTime.setText(DateTimeFormat.forPattern("HH:mm").print(time));
            dataTime.setInputType(InputType.TYPE_NULL);
        }
    }

    public int convertToSeconds(String time) {
        String hour_minute[] = time.split(":");
        int seconds = Integer.parseInt(hour_minute[0]) * 60 * 60 + Integer.parseInt(hour_minute[1]) * 60;
        return seconds;
    }

    public interface OnFragmentInteractionListener {
//        void setPublisher(String publisher);
//        void submitSubscription();
        void setMondayToSunday(int[] mondayToSundayArray);
        void setNotificationEnabled(int enabled);
        void setStartAndEndTime(int[] startAndEndTime);
        void goToThirdSubscriptionPageFromSecondPage();
        void goToFirstSubscriptionPageFromSecondPage();
    }

}
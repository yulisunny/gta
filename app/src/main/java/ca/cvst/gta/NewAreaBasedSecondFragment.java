package ca.cvst.gta;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.app.FragmentManager;


import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;

import java.util.ArrayList;
import java.util.Calendar;


public class NewAreaBasedSecondFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private OnFragmentInteractionListener mListener;
    private static View root;
    private ArrayAdapter<CharSequence> mPublishersAdapter;
    private Spinner mPublishersSpinner;

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

        ToggleButton toggleSunday = (ToggleButton) root.findViewById(R.id.Sunday);
        toggleSunday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // toggle enabled
                } else {
                    // toggle disabled
                }
            }
        });

        ToggleButton toggleMonday = (ToggleButton) root.findViewById(R.id.Monday);
        toggleMonday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // toggle enabled
                } else {
                    // toggle disabled
                }
            }
        });

        ToggleButton toggleTuesday = (ToggleButton) root.findViewById(R.id.Tuesday);
        toggleTuesday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // toggle enabled
                } else {
                    // toggle disabled
                }
            }
        });

        ToggleButton toggleWednesday = (ToggleButton) root.findViewById(R.id.Wednesday);
        toggleWednesday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // toggle enabled
                } else {
                    // toggle disabled
                }
            }
        });

        ToggleButton toggleThursday = (ToggleButton) root.findViewById(R.id.Thursday);
        toggleThursday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // toggle enabled
                } else {
                    // toggle disabled
                }
            }
        });

        ToggleButton toggleFriday = (ToggleButton) root.findViewById(R.id.Friday);
        toggleFriday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // toggle enabled
                } else {
                    // toggle disabled
                }
            }
        });

        ToggleButton toggleSaturday = (ToggleButton) root.findViewById(R.id.Saturday);
        toggleSaturday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    System.out.println(mPublishersSpinner.getSelectedItem().toString());
                    EditText startTime = (EditText) root.findViewById(R.id.startTime);
                    System.out.println(startTime.getText().toString());
                    EditText endTime = (EditText) root.findViewById(R.id.endTime);
                    System.out.println(endTime.getText().toString());
                    // toggle enabled
                } else {
                    // toggle disabled
                }
            }
        });


        mPublishersSpinner = (Spinner) root.findViewById(R.id.spinner_publisher);
        mPublishersAdapter = ArrayAdapter.createFromResource(root.getContext(), R.array.publishers_array, android.R.layout.simple_spinner_item);
        mPublishersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPublishersSpinner.setAdapter(mPublishersAdapter);
        mPublishersSpinner.setOnItemSelectedListener(this);

        EditText startTime = (EditText) root.findViewById(R.id.startTime);
        LocalTime currTime = new LocalTime();
        startTime.setText(DateTimeFormat.forPattern("HH:mm").print(currTime)); //default time
        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialogStartTime();
            }
        });

        EditText endTime = (EditText) root.findViewById(R.id.endTime);
        endTime.setText(DateTimeFormat.forPattern("HH:mm").print(currTime)); //default time
        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialogEndTime();
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
//        mListener = null;
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
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

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
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

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

    public interface OnFragmentInteractionListener {
        void nextPage();
    }

}
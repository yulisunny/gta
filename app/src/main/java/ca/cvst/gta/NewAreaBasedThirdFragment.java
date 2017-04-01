package ca.cvst.gta;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NewAreaBasedThirdFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private OnFragmentInteractionListener mListener;
    private ArrayAdapter<CharSequence> mPublishersAdapter;
    private Spinner mPublishersSpinner;
    private String publisher = null;
    private ArrayAdapter<CharSequence> mFieldNamesAdapter;
    private Spinner mFieldNamesSpinner;
    private EditText mFieldValueView;
    private Button mSubscribeButton;
    private Button mEnterButton;
    private static View root;
    private HashMap<String, Float> mAirSensorMap = new HashMap<>();
    private String mTtcRouteNumber = "-1";
    private String mAirType = "-1";
    private float mAirValue = -1;
    private RadioGroup mOperationsRadioGroup;
    private List<Filter> mFilters;
    private RecyclerView mFilterList;
    private FilterListAdapter mFilterListAdapter;
    private RecyclerView.LayoutManager mFilterListLayoutManager;


    public NewAreaBasedThirdFragment() {
    }

    public static NewAreaBasedThirdFragment newInstance() {
        NewAreaBasedThirdFragment fragment = new NewAreaBasedThirdFragment();
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
        root = inflater.inflate(R.layout.fragment_area_based_subscriptions_third_page, container, false);

        mPublishersSpinner = (Spinner) root.findViewById(R.id.spinner_publisher);
        mPublishersAdapter = ArrayAdapter.createFromResource(root.getContext(), R.array.publishers_array, android.R.layout.simple_spinner_item);
        mPublishersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPublishersSpinner.setAdapter(mPublishersAdapter);
        mPublishersSpinner.setOnItemSelectedListener(this);

        mFieldNamesSpinner = (Spinner) root.findViewById(R.id.spinner_field_names);
        mFieldNamesSpinner.setOnItemSelectedListener(this);

        mFieldValueView = (EditText) root.findViewById(R.id.edit_text_field_value);

        mSubscribeButton = (Button) root.findViewById(R.id.btn_subscribe);
        mSubscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (publisher != null) {
                    mListener.setPublisher(mPublishersSpinner.getSelectedItem().toString());
                    mListener.setRouteNumber(mTtcRouteNumber);
                    mListener.setAirTypeAndValue(mAirType, mAirValue);
                    mListener.setAirSensorMap(mAirSensorMap);

                    mListener.setFilterList(mFilters);
                    mListener.submitSubscription();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mSubscribeButton.setEnabled(true);

        mEnterButton = (Button) root.findViewById(R.id.btn_enter);
        mEnterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterFieldValue();
            }
        });
        mEnterButton.setEnabled(false);

        Button previousButton = (Button) root.findViewById(R.id.btn_previous);
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.goToSecondSubscriptionPageFromThirdPage();
            }
        });

        mOperationsRadioGroup = (RadioGroup) root.findViewById(R.id.radio_group_operation);
        RadioButton eqButton = (RadioButton) root.findViewById(R.id.radio_eq);
        eqButton.setChecked(true);


        mFilterList = (RecyclerView) root.findViewById(R.id.filter_list);
        mFilterListLayoutManager = new LinearLayoutManager(root.getContext());
        mFilterList.setLayoutManager(mFilterListLayoutManager);
        mFilters = new ArrayList<>();
        mFilterListAdapter = new FilterListAdapter(root.getContext(), mFilters);
        mFilterListAdapter.setOnDeleteFilterBtnClickListener(new FilterListAdapter.OnDeleteFilterBtnClickListener() {
            @Override
            public void onDeleteFilterBtnClick(View deleteBtn, int position) {
                mFilters.remove(position);
                mFilterListAdapter.notifyItemRemoved(position);
//                if (mFilters.isEmpty()) {
//                    mSubscribeButton.setEnabled(false);
//                }
            }
        });
        mFilterList.setAdapter(mFilterListAdapter);

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
        if (context instanceof NewAreaBasedThirdFragment.OnFragmentInteractionListener) {
            mListener = (NewAreaBasedThirdFragment.OnFragmentInteractionListener) context;
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

            switch (pos) {
                case 0:
                    mFieldNamesAdapter = ArrayAdapter.createFromResource(root.getContext(), R.array.ttc_field_array, android.R.layout.simple_spinner_item);
                    break;
                case 1:
                    mFieldNamesAdapter = ArrayAdapter.createFromResource(root.getContext(), R.array.air_sensor_field_array, android.R.layout.simple_spinner_item);
                    break;
            }
            mFieldNamesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mFieldNamesSpinner.setAdapter(mFieldNamesAdapter);
//            mFilters.clear();
//            mFilterListAdapter.notifyDataSetChanged();

            publisher = mPublishersSpinner.getSelectedItem().toString();

        }
        if (parent.getId() == R.id.spinner_field_names) {
            String fieldName = mFieldNamesSpinner.getSelectedItem().toString();
            String dataType = mPublishersSpinner.getSelectedItem().toString();
            if (!fieldName.equals("Everything")) {
//                mSubscribeButton.setEnabled(false);
                mEnterButton.setEnabled(true);
//                if (dataType.equals("TTC")) {
//                    mFieldValueView.setHint("");
//                }
//                else if (dataType.equals("Air Sensor")) {
//                    mFieldValueView.setHint("GreaterThan");
//                }
            }
            else {
                mFieldValueView.setHint("N/A");
                mSubscribeButton.setEnabled(true);
                mEnterButton.setEnabled(false);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void enterFieldValue() {
        String fieldValue = mFieldValueView.getText().toString();
        String fieldName = mFieldNamesSpinner.getSelectedItem().toString();

        String dataType = mPublishersSpinner.getSelectedItem().toString();
        if (dataType.equals("TTC")) {
            switch (fieldName) {
                case "Route Number":
                    fieldName = "routeNumber";
                    mTtcRouteNumber = fieldValue;
                    break;
                case "Everything":
                    mTtcRouteNumber = "-1";
                    break;
                default:
                    break;
            }
        }
        else if (dataType.equals("Air Sensor")) {
            switch (fieldName) {
                case "Carbon Dioxide (CO2)":
                    fieldName = "co2";
                    mAirSensorMap.put("co2", Float.parseFloat(fieldValue));
                    mAirType = "CO2";
                    mAirValue = Float.parseFloat(fieldValue);
                    break;
                case "Carbon Monoxide (CO)":
                    fieldName = "co";
                    mAirSensorMap.put("co", Float.parseFloat(fieldValue));
                    mAirType = "CO";
                    mAirValue = Float.parseFloat(fieldValue);
                    break;
                case "Nitrogen Oxides (NOx)":
                    fieldName = "nox";
                    mAirSensorMap.put("nox", Float.parseFloat(fieldValue));
                    mAirType = "NOx";
                    mAirValue = Float.parseFloat(fieldValue);
                    break;
                case "Air Quality Health Index (AQHI)":
                    fieldName = "aqhi";
                    mAirSensorMap.put("aqhi", Float.parseFloat(fieldValue));
                    mAirType = "AQHI";
                    mAirValue = Float.parseFloat(fieldValue);
                    break;
                case "Ozone (O3)":
                    fieldName = "o3";
                    mAirSensorMap.put("o3", Float.parseFloat(fieldValue));
                    mAirType = "O3";
                    mAirValue = Float.parseFloat(fieldValue);
                    break;
                case "Particulate Matter (PM)":
                    fieldName = "pm";
                    mAirSensorMap.put("pm", Float.parseFloat(fieldValue));
                    mAirType = "PM";
                    mAirValue = Float.parseFloat(fieldValue);
                    break;
                case "Everything":
                    mAirSensorMap.clear();
                    mAirType = "-1";
                    mAirValue = -1;
                    break;
                default:
                    break;
            }
        }

        if (TextUtils.isEmpty(fieldValue) && !fieldName.equals("Everything")) {
            mFieldValueView.setError(getString(R.string.new_subscription_invalid_field_value));
            return;
        }

        RadioButton checkedOperation = (RadioButton) root.findViewById(mOperationsRadioGroup.getCheckedRadioButtonId());
//
        Filter.Operation operation = Filter.Operation.valueOf(checkedOperation.getText().toString().toUpperCase());
        Filter newFilter = new Filter(fieldName, operation, fieldValue);
        if (checkNewFilter(newFilter)) {
            mFilters.add(0, newFilter);
            mFilterListAdapter.notifyItemInserted(0);
            mFilterList.scrollToPosition(0);
            mSubscribeButton.setEnabled(true);
        }

        mSubscribeButton.setEnabled(true);
    }


    public boolean checkNewFilter(Filter newFilter) {
        for (Filter existingFilter : mFilters) {
            if (TextUtils.equals(newFilter.getFieldName(), existingFilter.getFieldName()) &&
                    newFilter.getOperation() == existingFilter.getOperation() &&
                    TextUtils.equals(newFilter.getFieldValue(), existingFilter.getFieldValue())) {
                return false;
            }
        }
        return true;
    }

    public interface OnFragmentInteractionListener {
        void setPublisher(String publisher);
        void submitSubscription();
        void goToSecondSubscriptionPageFromThirdPage();
        void setRouteNumber(String routeNumber);
        void setAirSensorMap(HashMap<String, Float> airSensorMap);
        void setAirTypeAndValue(String airType, float airValue);
        void setFilterList(List<Filter> mFilters);
    }
}

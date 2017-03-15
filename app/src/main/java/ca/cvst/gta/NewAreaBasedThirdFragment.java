package ca.cvst.gta;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class NewAreaBasedThirdFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private OnFragmentInteractionListener mListener;
    private ArrayAdapter<CharSequence> mPublishersAdapter;
    private Spinner mPublishersSpinner;
    private String publisher = null;
    private ArrayAdapter<CharSequence> mFieldNamesAdapter;
    private Spinner mFieldNamesSpinner;
    private EditText mFieldValueView;
    private Button mSubscribeButton;



    private static View root;

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
                    mListener.submitSubscription();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mSubscribeButton.setEnabled(true);

        Button enterButton = (Button) root.findViewById(R.id.btn_enter);
        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterFieldValue();
            }
        });

        Button previousButton = (Button) root.findViewById(R.id.btn_previous);
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.goToSecondSubscriptionPageFromThirdPage();
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
            if (!fieldName.equals("Everything")) {
                mSubscribeButton.setEnabled(false);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void enterFieldValue() {
        String fieldValue = mFieldValueView.getText().toString();
        String fieldName = mFieldNamesSpinner.getSelectedItem().toString();

        if (TextUtils.isEmpty(fieldValue) && !fieldName.equals("Everything")) {
            mFieldValueView.setError(getString(R.string.new_subscription_invalid_field_value));
            return;
        }
        mSubscribeButton.setEnabled(true);
    }

    public interface OnFragmentInteractionListener {
        void setPublisher(String publisher);
        void submitSubscription();
        void goToSecondSubscriptionPageFromThirdPage();
    }
}

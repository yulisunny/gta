package ca.cvst.gta;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class NewSubscriptionActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    /**
     * Keep track of the subscription task to ensure we can cancel it if requested.
     */
    private SubscribeTask mSubscribeTask = null;

    // UI references.
    private ArrayAdapter<CharSequence> mPublishersAdapter;
    private Spinner mPublishersSpinner;
    private ArrayAdapter<CharSequence> mFieldNamesAdapter;
    private Spinner mFieldNamesSpinner;
    private EditText mFieldValueView;
    private View mProgressView;
    private View mSubscriptionFormView;
    private RecyclerView mFilterList;
    private FilterListAdapter mFilterListAdapter;
    private RecyclerView.LayoutManager mFilterListLayoutManager;
    private List<Filter> mFilters;
    private RadioGroup mOperationsRadioGroup;
    private Button mSubscribeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_subscription);
        setupActionBar();

        mPublishersSpinner = (Spinner) findViewById(R.id.spinner_publisher);
        mPublishersAdapter = ArrayAdapter.createFromResource(this, R.array.publishers_array, android.R.layout.simple_spinner_item);
        mPublishersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPublishersSpinner.setAdapter(mPublishersAdapter);
        mPublishersSpinner.setOnItemSelectedListener(this);

        mFieldNamesSpinner = (Spinner) findViewById(R.id.spinner_field_names);
        mFieldNamesSpinner.setOnItemSelectedListener(this);

        mFieldValueView = (EditText) findViewById(R.id.edit_text_field_value);

        Button mAddFilterButton = (Button) findViewById(R.id.btn_add_filter);
        mAddFilterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptAddFilter();
            }
        });

        mSubscribeButton = (Button) findViewById(R.id.btn_subscribe);
        mSubscribeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSubscribe();
            }
        });
        mSubscribeButton.setEnabled(false);

        mSubscriptionFormView = findViewById(R.id.subscription_form);
        mProgressView = findViewById(R.id.subscription_progress);

        mFilterList = (RecyclerView) findViewById(R.id.filter_list);
        mFilterListLayoutManager = new LinearLayoutManager(this);
        mFilterList.setLayoutManager(mFilterListLayoutManager);
        mFilters = new ArrayList<>();
        mFilterListAdapter = new FilterListAdapter(this, mFilters);
        mFilterListAdapter.setOnDeleteFilterBtnClickListener(new FilterListAdapter.OnDeleteFilterBtnClickListener() {
            @Override
            public void onDeleteFilterBtnClick(View deleteBtn, int position) {
                mFilters.remove(position);
                mFilterListAdapter.notifyItemRemoved(position);
                if (mFilters.isEmpty()) {
                    mSubscribeButton.setEnabled(false);
                }
            }
        });
        mFilterList.setAdapter(mFilterListAdapter);

        mOperationsRadioGroup = (RadioGroup) findViewById(R.id.radio_group_operation);
        RadioButton eqButton = (RadioButton) findViewById(R.id.radio_eq);
        eqButton.setChecked(true);
    }


    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        if (parent.getId() == R.id.spinner_publisher) {
            switch (pos) {
                case 0:
                    mFieldNamesAdapter = ArrayAdapter.createFromResource(this, R.array.ttc_field_names_array, android.R.layout.simple_spinner_item);
                    break;
                case 1:
                    mFieldNamesAdapter = ArrayAdapter.createFromResource(this, R.array.road_traffic_field_names_array, android.R.layout.simple_spinner_item);
                    break;
            }
            mFieldNamesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mFieldNamesSpinner.setAdapter(mFieldNamesAdapter);
            mFilters.clear();
            mFilterListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void attemptAddFilter() {
        String fieldValue = mFieldValueView.getText().toString();
        if (TextUtils.isEmpty(fieldValue)) {
            mFieldValueView.setError(getString(R.string.new_subscription_invalid_field_value));
            return;
        }
        String fieldName = mFieldNamesSpinner.getSelectedItem().toString();
        RadioButton checkedOperation = (RadioButton) findViewById(mOperationsRadioGroup.getCheckedRadioButtonId());
        Filter.Operation operation = Filter.Operation.valueOf(checkedOperation.getText().toString().toUpperCase());
        Filter newFilter = new Filter(fieldName, operation, fieldValue);
        mFilters.add(0, newFilter);
        mFilterListAdapter.notifyItemInserted(0);
        mFilterList.scrollToPosition(0);
        mSubscribeButton.setEnabled(true);
    }

    private void attemptSubscribe() {
        if (mSubscribeTask != null) {
            return;
        }
//        // Reset errors.
//        mEmailView.setError(null);
//        mPasswordView.setError(null);
//
//        View focusView = null;
//        focusView.requestFocus();
        showProgress(true);
        mSubscribeTask = new SubscribeTask(mFilters);
        mSubscribeTask.execute((Void) null);

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mSubscriptionFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mSubscriptionFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mSubscriptionFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mSubscriptionFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public class SubscribeTask extends AsyncTask<Void, Void, Boolean> {

        private final List<Filter> mFilters;

        SubscribeTask(List<Filter> filters) {
            mFilters = filters;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mSubscribeTask = null;
            showProgress(false);
            if (success) {
                Toast.makeText(getApplicationContext(), "Subscribed!", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Subscription failed.", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mSubscribeTask = null;
            showProgress(false);
        }
    }
}


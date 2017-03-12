package ca.cvst.gta;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
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

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
            mSubscribeButton.setEnabled(false);
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
        if (checkNewFilter(newFilter)) {
            mFilters.add(0, newFilter);
            mFilterListAdapter.notifyItemInserted(0);
            mFilterList.scrollToPosition(0);
            mSubscribeButton.setEnabled(true);
        }
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

    private void attemptSubscribe() {
//        showProgress(true);
        try {
            JSONArray mustArray = new JSONArray();
            for (Filter filter : mFilters) {
                if (filter.getOperation() == Filter.Operation.EQ) {
                    JSONObject o1 = new JSONObject();
                    o1.put(filter.getFieldName(), filter.getFieldValue());
                    JSONObject o2 = new JSONObject();
                    o2.put("match", o1);
                    mustArray.put(o2);
                } else {
                    JSONObject o1 = new JSONObject();
                    o1.put(filter.getOperation().toString().toLowerCase(), Float.valueOf(filter.getFieldValue()));
                    JSONObject o2 = new JSONObject();
                    o2.put(filter.getFieldName(), o1);
                    JSONObject o3 = new JSONObject();
                    o3.put("range", o2);
                    mustArray.put(o3);
                }
            }
            JSONObject boolObject = new JSONObject().put("must", mustArray);
            JSONObject subscriptionObject = new JSONObject().put("bool", boolObject);
            final JSONObject payload = new JSONObject();
            payload.put("publisherName", mPublishersSpinner.getSelectedItem().toString());
            payload.put("subscription", subscriptionObject);
            payload.put("action", "subscribe");
//            payload.put("ttl", "5m");
            System.out.println("payload = " + payload);
            SubscriptionService.startActionSubscribe(getApplicationContext(), payload.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        Demo portal code.
//        JsonObjectRequest request = new JsonObjectRequest("http://subs.portal.cvst.ca/api/subscribe", payload, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                System.out.println("response = " + response);
//                showProgress(false);
//                String status = "error";
//                String message = "There was an error, please try again.";
//                try {
//                    status = response.getString("status");
//                    message = response.getString("message");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
//                if (status.equals("success")) {
//                    finish();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                System.out.println("error = " + error);
//                Toast.makeText(getApplicationContext(), "Subscription failed.", Toast.LENGTH_LONG).show();
//            }
//        });
//        NetworkManager.getInstance(this).addToRequestQueue(request);


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

        private final String mPublisher;
        private final List<Filter> mFilters;
        private Context mContext;

        SubscribeTask(String publisher, List<Filter> filters, Context context) {
            mPublisher = publisher;
            mFilters = filters;
            mContext = context;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            JSONObject payload = null;
            try {
                JSONArray mustArray = new JSONArray();
                for (Filter filter : mFilters) {
                    if (filter.getOperation() == Filter.Operation.EQ) {
                        JSONObject o1 = new JSONObject();
                        o1.put(filter.getFieldName(), filter.getFieldValue());
                        JSONObject o2 = new JSONObject();
                        o2.put("match", o1);
                        mustArray.put(o2);
                    } else {
                        JSONObject o1 = new JSONObject();
                        o1.put(filter.getOperation().toString().toLowerCase(), Float.valueOf(filter.getFieldValue()));
                        JSONObject o2 = new JSONObject();
                        o2.put(filter.getFieldName(), o1);
                        JSONObject o3 = new JSONObject();
                        o3.put("range", o2);
                        mustArray.put(o3);
                    }
                }
                JSONObject boolObject = new JSONObject().put("must", mustArray);
                JSONObject subscriptionObject = new JSONObject().put("bool", boolObject);
                payload = new JSONObject();
                payload.put("publisherName", mPublisher);
                payload.put("subscription", subscriptionObject);
                payload.put("ttl", "1d");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            System.out.println("payload = " + payload);
            try {
                JsonObjectRequest request = new JsonObjectRequest("http://subs.portal.cvst.ca/api/subscribe", payload, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("response = " + response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("error = " + error);

                    }
                });
                NetworkManager.getInstance(mContext).addToRequestQueue(request);
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


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

/**
 * A login screen that offers login via email/password.
 */
public class NewSubscriptionActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private SubscribeTask mSubscribeTask = null;

    // UI references.
    private ArrayAdapter<CharSequence> mPublishersAdapter;
    private Spinner mPublishersSpinner;
    private ArrayAdapter<CharSequence> mFieldNamesAdapter;
    private Spinner mFieldNamesSpinner;
    private EditText mFieldValueView;
    private View mProgressView;
    private View mLoginFormView;
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

        // Set up the login form.
        mPublishersSpinner = (Spinner) findViewById(R.id.spinner_publisher);
        mPublishersAdapter = ArrayAdapter.createFromResource(this, R.array.publishers_array, android.R.layout.simple_spinner_item);
        mPublishersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPublishersSpinner.setAdapter(mPublishersAdapter);
        mPublishersSpinner.setOnItemSelectedListener(this);

        mFieldNamesSpinner = (Spinner) findViewById(R.id.spinner_field_names);
        mFieldNamesSpinner.setOnItemSelectedListener(this);

        mFieldValueView = (EditText) findViewById(R.id.edit_text_field_value);

//        mPasswordView = (EditText) findViewById(R.id.password);
//        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
//                if (id == R.id.login || id == EditorInfo.IME_NULL) {
//                    attemptSubscribe();
//                    return true;
//                }
//                return false;
//            }
//        });

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

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

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
                if(mFilters.isEmpty()) {
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
        System.out.println("parent = " + parent);
        System.out.println("view = " + view);
        System.out.println("pos = " + pos);
        System.out.println("id = " + id);
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
        // Another interface callback
        System.out.println("nothing selected called");
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
//        CardView cardViewWrapper = (CardView) getLayoutInflater().inflate(R.layout.card_filter, mFilterList, false);
//        TextView filter = (TextView) cardViewWrapper.findViewById(R.id.text_filter);
//        filter.setText(mFieldNamesSpinner.getSelectedItem().toString() + "=" + fieldValue);
//        mFilterList.addView(cardViewWrapper);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptSubscribe() {
//        if (mSubscribeTask != null) {
//            return;
//        }
//
//        // Reset errors.
//        mEmailView.setError(null);
//        mPasswordView.setError(null);
//
//        // Store values at the time of the login attempt.
//        String email = mEmailView.getText().toString();
//        String password = mPasswordView.getText().toString();
//
//        boolean cancel = false;
//        View focusView = null;
//
//        // Check for a valid password, if the user entered one.
//        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
//            mPasswordView.setError(getString(R.string.error_invalid_password));
//            focusView = mPasswordView;
//            cancel = true;
//        }
//
//        // Check for a valid email address.
//        if (TextUtils.isEmpty(email)) {
//            mEmailView.setError(getString(R.string.error_field_required));
//            focusView = mEmailView;
//            cancel = true;
//        } else if (!isEmailValid(email)) {
//            mEmailView.setError(getString(R.string.error_invalid_email));
//            focusView = mEmailView;
//            cancel = true;
//        }
//
//        if (cancel) {
//            // There was an error; don't attempt login and focus the first
//            // form field with an error.
//            focusView.requestFocus();
//        } else {
//            // Show a progress spinner, and kick off a background task to
//            // perform the user login attempt.
//            showProgress(true);
//            mSubscribeTask = new SubscribeTask(email, password);
//            mSubscribeTask.execute((Void) null);
//        }
        showProgress(true);
        mSubscribeTask = new SubscribeTask(mFilters);
        mSubscribeTask.execute((Void) null);

    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
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


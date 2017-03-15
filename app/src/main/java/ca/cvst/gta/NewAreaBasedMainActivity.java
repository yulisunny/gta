package ca.cvst.gta;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.model.LatLngBounds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ca.cvst.gta.db.DbHelper;
import ca.cvst.gta.db.TtcSubscriptionsContract.TtcSubscriptionEntry;

public class NewAreaBasedMainActivity extends AppCompatActivity
        implements NewAreaBasedFirstFragment.OnFragmentInteractionListener,
        NewAreaBasedSecondFragment.OnFragmentInteractionListener, NewAreaBasedThirdFragment.OnFragmentInteractionListener {

    private LatLngBounds AreaBounds;
    private String publisher;
    private int[] mondayToSundayArray;
    private int notificationEnabled;
    private int[] startAndEndTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_based_subscription_main_page);

        NewAreaBasedFirstFragment firstFragment = NewAreaBasedFirstFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_area_based_content_container, firstFragment).commit();
    }

    @Override
    public void setCoordinates(LatLngBounds bounds) {
        this.AreaBounds = bounds;
    }

    @Override
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    @Override
    public void setMondayToSunday(int[] mondayToSundayArray) {
        this.mondayToSundayArray = mondayToSundayArray;
    }

    @Override
    public void setNotificationEnabled(int enabled) {
        this.notificationEnabled = enabled;
    }

    @Override
    public void setStartAndEndTime(int[] startAndEndTime) {
        this.startAndEndTime = startAndEndTime;
    }

    @Override
    public void submitSubscription() {
        attemptSubscribe();
//        Intent intent = new Intent(getApplicationContext(), SubscriptionsActivity.class);
//        startActivity(intent);
    }

    @Override
    public void goToFirstSubscriptionPageFromSecondPage() {
        NewAreaBasedFirstFragment firstFragment = NewAreaBasedFirstFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_area_based_content_container, firstFragment).commit();
    }

    @Override
    public void goToThirdSubscriptionPageFromSecondPage() {
        NewAreaBasedThirdFragment thirdFragment = NewAreaBasedThirdFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_area_based_content_container, thirdFragment).commit();
    }

    @Override
    public void goToSecondSubscriptionPageFromThirdPage() {
        NewAreaBasedSecondFragment secondFragment = NewAreaBasedSecondFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_area_based_content_container, secondFragment).commit();
    }

    @Override
    public void goToSecondSubscriptionPageFromFirstPage() {
        NewAreaBasedSecondFragment secondFragment = NewAreaBasedSecondFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_area_based_content_container, secondFragment).commit();
    }

    private void attemptSubscribe() {
//        showProgress(true);
        final JSONObject payload = new JSONObject();
        final double upperLongitude = AreaBounds.northeast.longitude;
        final double lowerLongitude = AreaBounds.southwest.longitude;
        final double upperLatitude = AreaBounds.northeast.latitude;
        final double lowerLatitude = AreaBounds.southwest.latitude;

        try {
//            JSONArray mustArray = new JSONArray();
//            for (Filter filter : mFilters) {
//                if (filter.getOperation() == Filter.Operation.EQ) {
//                    JSONObject o1 = new JSONObject();
//                    o1.put(filter.getFieldName(), filter.getFieldValue());
//                    JSONObject o2 = new JSONObject();
//                    o2.put("match", o1);
//                    mustArray.put(o2);
//                } else {
//                    JSONObject o1 = new JSONObject();
//                    o1.put(filter.getOperation().toString().toLowerCase(), Float.valueOf(filter.getFieldValue()));
//                    JSONObject o2 = new JSONObject();
//                    o2.put(filter.getFieldName(), o1);
//                    JSONObject o3 = new JSONObject();
//                    o3.put("range", o2);
//                    mustArray.put(o3);
//                }
//            }

            JSONObject lngObject = new JSONObject().put("gt", lowerLongitude).put("lt", upperLongitude);
            JSONObject latObject = new JSONObject().put("gt", lowerLatitude).put("lt", upperLatitude);
            JSONObject lngCoordinateObject = new JSONObject().put("coordinates", lngObject);
            JSONObject latCoordinateObject = new JSONObject().put("coordinates", latObject);
            JSONObject lngRangeObject = new JSONObject().put("range", lngCoordinateObject);
            JSONObject latRangeObject = new JSONObject().put("range", latCoordinateObject);

            JSONArray mustArray = new JSONArray();
            mustArray.put(lngRangeObject);
            mustArray.put(latRangeObject);

            JSONObject mustObject = new JSONObject().put("must", mustArray);
            JSONObject boolObject = new JSONObject().put("bool", mustObject);

            // publisher = mPublishersSpinner.getSelectedItem().toString();
            payload.put("publisherName", publisher);
            payload.put("subscription", boolObject);
            payload.put("action", "subscribe");
//            payload.put("ttl", "5m");
            System.out.println("payload = " + payload);
//            SubscriptionService.startActionSubscribe(getApplicationContext(), payload.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        Demo portal code.
        JsonObjectRequest request = new JsonObjectRequest("http://subs.portal.cvst.ca/api/subscribe", payload, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println("response = " + response);
                //showProgress(false);
                String status = "error";
                String message = "There was an error, please try again.";
                try {
                    status = response.getString("status");
                    message = response.getString("message");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                if (status.equals("success")) {
                    DbHelper helper = new DbHelper(getApplicationContext());
                    SQLiteDatabase db = helper.getWritableDatabase();
                    ContentValues cv = new ContentValues();
                    if (publisher.equals("TTC")) {
                        try {
                            cv.put(TtcSubscriptionEntry.TIMESTAMP, System.currentTimeMillis()/1000L);
                            cv.put(TtcSubscriptionEntry.NAME, response.getString("subscription_id"));
                            cv.put(TtcSubscriptionEntry.LOWER_LATITUDE, lowerLatitude);
                            cv.put(TtcSubscriptionEntry.UPPER_LATITUDE, upperLatitude);
                            cv.put(TtcSubscriptionEntry.LOWER_LONGITUDE, lowerLongitude);
                            cv.put(TtcSubscriptionEntry.UPPER_LONGITUDE, upperLongitude);
                            cv.put(TtcSubscriptionEntry.ROUTE_NUMBER, "9");
                            cv.put(TtcSubscriptionEntry.MONDAY, mondayToSundayArray[0]);
                            cv.put(TtcSubscriptionEntry.TUESDAY, mondayToSundayArray[1]);
                            cv.put(TtcSubscriptionEntry.WEDNESDAY, mondayToSundayArray[2]);
                            cv.put(TtcSubscriptionEntry.THURSDAY, mondayToSundayArray[3]);
                            cv.put(TtcSubscriptionEntry.FRIDAY, mondayToSundayArray[4]);
                            cv.put(TtcSubscriptionEntry.SATURDAY, mondayToSundayArray[5]);
                            cv.put(TtcSubscriptionEntry.SUNDAY, mondayToSundayArray[6]);
                            cv.put(TtcSubscriptionEntry.START_TIME, startAndEndTime[0]);
                            cv.put(TtcSubscriptionEntry.END_TIME, startAndEndTime[1]);
                            cv.put(TtcSubscriptionEntry.NOTIFICATION_ENABLED, notificationEnabled);
                            cv.put(TtcSubscriptionEntry.SUBSCRIPTION_TYPE, "Area Based");
                            cv.put(TtcSubscriptionEntry.SUBSCRIPTION_ID, response.getString("subscription_id"));
                            db.insert(TtcSubscriptionEntry.TABLE_NAME, null, cv);
                            db.close();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    Intent intent = new Intent(getApplicationContext(), SubscriptionsActivity.class);
                    startActivity(intent);
                    //finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("error = " + error);
                Toast.makeText(getApplicationContext(), "Subscription failed.", Toast.LENGTH_LONG).show();
            }
        });
        NetworkManager.getInstance(this).addToRequestQueue(request);


    }
//03-14 23:01:42.436 15442-15442/ca.cvst.gta I/System.out: payload = {"action":"subscribe","subscription":{"bool":{"must":[{"range":{"coordinates":{"gt":-79.39498620191746,"lt":-79.3701255891568}}},{"range":{"coordinates":{"gt":43.64761995081081,"lt":43.665606357245096}}}]}},"publisherName":"TTC"}
//    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
//    private void showProgress(final boolean show) {
//        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
//        // for very easy animations. If available, use these APIs to fade-in
//        // the progress spinner.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
//            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
//
//            mSubscriptionFormView.setVisibility(show ? View.GONE : View.VISIBLE);
//            mSubscriptionFormView.animate().setDuration(shortAnimTime).alpha(
//                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    mSubscriptionFormView.setVisibility(show ? View.GONE : View.VISIBLE);
//                }
//            });
//
//            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
//            mProgressView.animate().setDuration(shortAnimTime).alpha(
//                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
//                }
//            });
//        } else {
//            // The ViewPropertyAnimator APIs are not available, so simply show
//            // and hide the relevant UI components.
//            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
//            mSubscriptionFormView.setVisibility(show ? View.GONE : View.VISIBLE);
//        }
//    }
}

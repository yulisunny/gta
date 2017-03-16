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

public class NewIntersectionBasedMainActivity extends AppCompatActivity
        implements NewIntersectionBasedFirstFragment.OnFragmentInteractionListener,
        NewAreaBasedSecondFragment.OnFragmentInteractionListener,
        NewAreaBasedThirdFragment.OnFragmentInteractionListener{

    private LatLngBounds AreaBounds;
    private String publisher;
    private int[] mondayToSundayArray;
    private int notificationEnabled;
    private int[] startAndEndTime;
    private String intersectionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intersection_based_subscripton_main_page);

        NewIntersectionBasedFirstFragment firstFragment = NewIntersectionBasedFirstFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_intersection_based_content_container, firstFragment).commit();
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
    public void setIntersectionName(String name) {
        this.intersectionName = name;
    }

    @Override
    public void submitSubscription() {
        attemptSubscribe();
    }

    @Override
    public void goToFirstSubscriptionPageFromSecondPage() {
        NewIntersectionBasedFirstFragment firstFragment = NewIntersectionBasedFirstFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_intersection_based_content_container, firstFragment).commit();
    }

    @Override
    public void goToThirdSubscriptionPageFromSecondPage() {
        NewAreaBasedThirdFragment thirdFragment = NewAreaBasedThirdFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_intersection_based_content_container, thirdFragment).commit();
    }

    @Override
    public void goToSecondSubscriptionPageFromThirdPage() {
        NewAreaBasedSecondFragment secondFragment = NewAreaBasedSecondFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_intersection_based_content_container, secondFragment).commit();
    }

    @Override
    public void goToSecondSubscriptionPageFromFirstPage() {
        NewAreaBasedSecondFragment secondFragment = NewAreaBasedSecondFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_intersection_based_content_container, secondFragment).commit();
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
                            cv.put(TtcSubscriptionEntry.NAME, intersectionName);
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
                            cv.put(TtcSubscriptionEntry.SUBSCRIPTION_TYPE, "Intersection Based");
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
}
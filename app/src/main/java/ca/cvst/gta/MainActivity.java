package ca.cvst.gta;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import layout.UpdatesListenerIntentService;

public class MainActivity extends AppCompatActivity implements
        HomeMapFragment.OnFragmentInteractionListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    // For ttc:
    private Bitmap ttcIcon;
    private ArrayList<Marker> ttcMarkers;
    private Map<Integer, Integer> ttcInvertedIndex;
    private boolean ttcIsChecked = false;
    //private Handler ttcHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                Toast.makeText(getApplicationContext(), String.valueOf(tabId), Toast.LENGTH_LONG).show();
            }
        });


        if (savedInstanceState == null) {
            HomeMapFragment mapFragment = HomeMapFragment.newInstance("blah", "lori");
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.contentContainer, mapFragment).commit();
        }

//        loginAndListenForNotif();
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(), "Back Pressed", Toast.LENGTH_LONG).show();
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setActionBar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
    }

    private void loginAndListenForNotif() {
        StringRequest loginRequest = new StringRequest(Request.Method.POST, "http://subs.portal.cvst.ca/login", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("response = " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("error = " + error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", "yulisunny");
                params.put("password", "fang9443");
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }

        };
        NetworkManager.getInstance(this).addToRequestQueue(loginRequest);
        Intent updatesIntent = new Intent(getApplicationContext(), UpdatesListenerIntentService.class);
        startService(updatesIntent);
    }

}

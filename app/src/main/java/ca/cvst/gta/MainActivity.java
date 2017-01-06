package ca.cvst.gta;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener,
        OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    //private Marker mMarker;
    private JSONArray ttcInfoArray;
    private int ttcInfoArrayLength;
    private int index;

    // Boolean telling us whether a download is in progress, so we don't trigger overlapping
    // downloads with consecutive button clicks.
    //private boolean mDownloading = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string
                .navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        grabTTCdata();

        //LatLng toronto = new LatLng(43.6543, -79.3860);
        //mMarker = mMap.addMarker(new MarkerOptions()
        //        .position(toronto)
        //        .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("ttc",25,25)))
        //        .title("Marker in Toronto"));

        //System.out.println(ttcInfoArray);
       // mMarker.setTag("data set in onMapReady");
       // mMap.setOnMarkerClickListener(this);
        //plotTTC();

        //mMap.moveCamera(CameraUpdateFactory.newLatLng(toronto));

        //mMap.setTrafficEnabled(true);
        //mMap.setBuildingsEnabled(true);
        //mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        // Retrieve the data from the marker.
        String data = marker.getTag().toString();

        Toast.makeText(this, data, Toast.LENGTH_SHORT).show();

        // Check if a click count was set, then display the click count.
      /*  if (clickCount != null) {
            clickCount = clickCount + 1;
            marker.setTag(clickCount);
            Toast.makeText(this,
                    marker.getTitle() +
                            " has been clicked " + clickCount + " times.",
                    Toast.LENGTH_SHORT).show();
        }*/

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_past_notifications) {
            // Handle the camera action
        } else if (id == R.id.nav_historical_data_dashboard) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View view) {
       /* String url = "http://portal.cvst.ca/api/0.1/ttc";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray ttcVehicles) {
                        for (int i = 0; i < ttcVehicles.length(); ++i) {
                            try {
                                JSONObject ttcVehicle = ttcVehicles.getJSONObject(i);
                                System.out.println("ttcVehicle = " + ttcVehicle);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("error = " + error);
            }
        });
        NetworkManager.getInstance(this).addToRequestQueue(jsonArrayRequest);*/
        //plotTTC();
    }


    private void grabTTCdata(){
        String url = "http://portal.cvst.ca/api/0.1/ttc";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray ttcVehicles) {
                        ttcInfoArrayLength = ttcVehicles.length();
                        ttcInfoArray = ttcVehicles;
                        index = 0;
                        plotTTC();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("error = " + error);
            }
        });

        NetworkManager.getInstance(this).addToRequestQueue(jsonArrayRequest);
    }

    private Bitmap resizeMapIcons(String iconName, int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    private void plotTTC(){
        if (ttcInfoArrayLength <= 0) return;

        try {
            JSONObject ttcVehicle = ttcInfoArray.getJSONObject(index);
            JSONArray coordinates = ttcVehicle.getJSONArray("coordinates");
            LatLng location = new LatLng(coordinates.getDouble(1), coordinates.getDouble(0));
            mMap.addMarker(new MarkerOptions()
                    .position(location)
                    .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("ttc", 25, 25)))
                    .title("Marker2 in Toronto"));

            (new Handler()).postDelayed(new Runnable(){
                @Override
                public void run() {
                    index = index + 1;
                    plotTTC();
                }
            }, 1);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}

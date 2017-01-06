package ca.cvst.gta;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener,
        OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    // For ttc:
    private Bitmap ttcIcon;
    private JSONArray ttcInfoArray;
    private int ttcInfoArrayLength;
    private int index;
    private ArrayList<Marker> ttcMarkers;
    private Map<Integer, Integer> ttcInvertedIndex;


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

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                LatLng currentLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
                //mMap.addMarker(new MarkerOptions().position(currentLocation).title("Where You Are"));
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
        initialize_ttcData();

        //LatLng toronto = new LatLng(43.6543, -79.3860);
        //mMarker = mMap.addMarker(new MarkerOptions()
        //        .position(toronto)
        //        .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("ttc",25,25)))
        //        .title("Marker in Toronto"));

       // mMarker.setTag("data set in onMapReady");
       // mMap.setOnMarkerClickListener(this);

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
        Intent intent = new Intent(this, NewSubscriptionActivity.class);
        startActivity(intent);
    }

    private void initialize_ttcData(){
        ttcIcon = resizeMapIcons("ttc", 25, 25);
        ttcMarkers = new ArrayList<Marker>();
        ttcInvertedIndex = new HashMap<Integer, Integer>();

        String url = "http://portal.cvst.ca/api/0.1/ttc";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray ttcVehicles) {
                        ttcInfoArrayLength = ttcVehicles.length();
                        ttcInfoArray = ttcVehicles;
                        index = 0;
                        ttcPlotRecursive();
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

    private void ttcPlotRecursive(){
        if (index >= ttcInfoArrayLength){
            WebSocketConn();
            return;
        }
        try {
            /* portal.cvst.ca/api/0.1/ttc
                {
                    "GPStime": 1483740522,
                    "coordinates": [
                        -79.434685,
                        43.680168
                    ],
                    "dateTime": "Fri, 06 Jan 2017 22:09:00 -0000",
                    "dirTag": "63_0_63B",
                    "heading": "216",
                    "last_update": "Fri, 06 Jan 2017 22:08:59 -0000",
                    "predictable": true,
                    "routeNumber": "63",
                    "route_name": "63-Ossington",
                    "timestamp": 1483740540,
                    "vehicle_id": 1000
                }, */

            JSONObject ttcVehicle = ttcInfoArray.getJSONObject(index);
            JSONArray coordinates = ttcVehicle.getJSONArray("coordinates");
            int vehicle_id = ttcVehicle.getInt("vehicle_id");
            String route_name = ttcVehicle.getString("route_name");
            ttcInvertedIndex.put(vehicle_id, index);
            //String routeNumber = ttcVehicle.getString("routeNumber");
            LatLng location = new LatLng(coordinates.getDouble(1), coordinates.getDouble(0));
            ttcMarkers.add(mMap.addMarker(new MarkerOptions()
                    .position(location)
                    .icon(BitmapDescriptorFactory.fromBitmap(ttcIcon))
                    .title(route_name).snippet("Bus ID: " + vehicle_id)));

            // create a new thread to handle other markers so that the user doesn't need to wait on main thread to load all the data
            (new Handler()).postDelayed(new Runnable(){
                @Override
                public void run() {
                    index = index + 1;
                    ttcPlotRecursive();
                }
            }, 1);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void WebSocketConn(){
        AsyncHttpClient.getDefaultInstance().websocket("ws://subs.portal.cvst.ca:8888/websocket", null, new AsyncHttpClient.WebSocketConnectCallback() {
            @Override
            public void onCompleted(final Exception ex, WebSocket webSocket) {
                if (ex != null) {
                    ex.printStackTrace();
                    return;
                }
                webSocket.send("{\"action\": \"subscribe\", \"publisherName\": \"ttc\", \"subscription\": {\"bool\": {\"must\": []}}}");
                webSocket.setStringCallback(new WebSocket.StringCallback() {
                    public void onStringAvailable(String s) {
                        Handler handler = new Handler(Looper.getMainLooper());
                        try{
                            final JSONObject ttcVehicle = new JSONObject(s);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        // a data format:
                                        // {"id":8526,
                                        // "timestamp":1483736583,
                                        // "routeNumber":"35",
                                        // "category":"ttc",
                                        // "predictable":true,
                                        // "dateTime":"2017-01-06 21:03:03+00:00",
                                        // "name":"35-Jane",
                                        // "lastTime":"2017-01-06 21:03:01+00:00",
                                        // "GPStime":1483736571,"dirTag":"35_0_35D",
                                        // "heading":"162",
                                        // "coordinates":[-79.531799,43.7945179]}

                                        JSONObject data = ttcVehicle.getJSONObject("data");
                                        int vehicle_id = data.getInt("id");
                                        if (ttcInvertedIndex.containsKey(vehicle_id)) {
                                            int arrayIndex = ttcInvertedIndex.get(vehicle_id);
                                            Marker m = ttcMarkers.get(arrayIndex);
                                            JSONArray coordinates = data.getJSONArray("coordinates");
                                            LatLng location = new LatLng(coordinates.getDouble(1), coordinates.getDouble(0));
                                            m.setPosition(location);
                                        } else {
                                            ttcInvertedIndex.put(vehicle_id, index);
                                            JSONArray coordinates = data.getJSONArray("coordinates");
                                            String route_name = data.getString("name");
                                            LatLng location = new LatLng(coordinates.getDouble(1), coordinates.getDouble(0));
                                            ttcMarkers.add(mMap.addMarker(new MarkerOptions()
                                                    .position(location)
                                                    .icon(BitmapDescriptorFactory.fromBitmap(ttcIcon))
                                                    .title(route_name).snippet("Bus ID: " + vehicle_id)));
                                            //System.out.println("index: " + index);
                                            index = index + 1;
                                            //System.out.println("length: " + ttcMarkers.size());
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                webSocket.setDataCallback(new DataCallback() {
                    public void onDataAvailable(DataEmitter emitter, ByteBufferList byteBufferList) {
                        System.out.println("I got some bytes!");
                        // note that this data has been read
                        byteBufferList.recycle();
                    }
                });
            }
        });
    }

}

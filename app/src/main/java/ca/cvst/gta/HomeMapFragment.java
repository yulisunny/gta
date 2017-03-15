package ca.cvst.gta;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeMapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeMapFragment extends Fragment implements
        NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks,
        OnMapReadyCallback {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private boolean ttcIsChecked = false;
    private ArrayList<Marker> ttcMarkers;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private GoogleMap mMap;
    private Bitmap ttcIcon;
    private Map<Integer, Integer> ttcInvertedIndex;
    private MapView mMapView;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Marker previousSearchedMarker = null;

    private OnFragmentInteractionListener mListener;

    public HomeMapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeMapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeMapFragment newInstance(String param1, String param2) {
        HomeMapFragment fragment = new HomeMapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        System.out.println("container = " + container);

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        mListener.setActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) rootView.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(), drawer, toolbar, R.string.navigation_drawer_open, R.string
                .navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) rootView.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        MenuItem ttc = navigationView.getMenu().findItem(R.id.ttc);
        ttc.setChecked(false);

        mMapView = (MapView) rootView.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        Button searchBtn = (Button) rootView.findViewById(R.id.home_search_button);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSearch(v);
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        mMapView.onStart();
        super.onStart();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        mMapView.onStop();
        super.onStop();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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
        mMapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.ttc) {
            // ttc insert here
            if (item.isChecked()) {
                item.setChecked(false);
                ttcIsChecked = false;
            } else {
                item.setChecked(true);
                ttcIsChecked = true;
            }

            for (Marker ttcMarker : ttcMarkers) {
                ttcMarker.setVisible(ttcIsChecked);
            }
        }
        DrawerLayout drawer = (DrawerLayout) getView().findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        System.out.println("on connected called");
        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            System.out.println("Have permission to access to location");
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                LatLng currentLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
        mMap.setTrafficEnabled(true);

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                Context mContext = getContext();
                LinearLayout info = new LinearLayout(mContext);
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(mContext);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(mContext);
                snippet.setTextColor(Color.BLACK);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });

        initialize_ttcData();

        //LatLng toronto = new LatLng(43.6543, -79.3860);
        //mMarker = mMap.addMarker(new MarkerOptions()
        //        .position(toronto)
        //        .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("ttc",25,25)))
        //        .title("Marker in Toronto"));

        // mMarker.setTag("data set in onMapReady");
        // mMap.setOnMarkerClickListener(this);

        //mMap.moveCamera(CameraUpdateFactory.newLatLng(toronto));

        //mMap.setBuildingsEnabled(true);
        //mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    private void initialize_ttcData() {
        ttcIcon = resizeMapIcons("ttc", 25, 25);
        ttcMarkers = new ArrayList<Marker>();
        ttcInvertedIndex = new HashMap<Integer, Integer>();

        String url = "http://portal.cvst.ca/api/0.1/ttc";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray ttcVehicles) {
                        ttcPlotNearby(ttcVehicles);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("error = " + error);
            }
        });
        NetworkManager.getInstance(getContext()).addToRequestQueue(jsonArrayRequest);
    }

    private Bitmap resizeMapIcons(String iconName, int width, int height) {
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(iconName, "drawable", getContext().getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    private void ttcPlotNearby(JSONArray ttcVehicles) {
        try {
            int ttcMarkersIndex = 0;
            ArrayList<JSONObject> ttcVehiclesSecondary = new ArrayList<JSONObject>();

            for (int index = 0; index < ttcVehicles.length(); index++) {
                JSONObject ttcVehicle = ttcVehicles.getJSONObject(index);
                JSONArray coordinates = ttcVehicle.getJSONArray("coordinates");
                double Latitude = coordinates.getDouble(1);
                double Longitude = coordinates.getDouble(0);

//                if (Helper.isNearby(mLastLocation.getLatitude(), mLastLocation.getLongitude(), Latitude, Longitude)) {
//                    ttcPlotMarker(ttcVehicle, ttcMarkersIndex, Latitude, Longitude);
//                    ttcMarkersIndex = ttcMarkersIndex + 1;
//                }
//                else {
                ttcVehiclesSecondary.add(ttcVehicle);
//                }
            }
            ttcPlotFurtherRecursive(ttcVehiclesSecondary, ttcMarkersIndex, 0);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void ttcPlotMarker(JSONObject ttcVehicle, int index, double Lat, double Long) {
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

        try {
            final LatLng location = new LatLng(Lat, Long);

            // get current route name
            String route_name = ttcVehicle.getString("route_name");

            // get current date and time
            String dateTime = Helper.convertTimestampToString(ttcVehicle.getLong("GPStime"));

            // calculate the direction based on the heading
            String direction = Helper.calculateDirection(Integer.parseInt(ttcVehicle.getString("heading")));

            // get the vehicle id and store it in inverted index table and add it to ttcMarker array
            int vehicle_id = ttcVehicle.getInt("vehicle_id");
            ttcInvertedIndex.put(vehicle_id, index);
            ttcMarkers.add(mMap.addMarker(new MarkerOptions()
                    .position(location)
                    .icon(BitmapDescriptorFactory.fromBitmap(ttcIcon))
                    .title(route_name)
                    .snippet("Bus ID: " + vehicle_id + '\n' + "Direction: " + direction + '\n' + "Time: " + dateTime)
                    .visible(ttcIsChecked)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void ttcPlotFurtherRecursive(final ArrayList<JSONObject> ttcVehicles, int ttcMarkerIndex, final int ttcSecondaryArrayIndex) {
        if (ttcMarkerIndex >= ttcVehicles.size()) {
            WebSocketConn();
            return;
        }
        try {
            JSONObject ttcVehicle = ttcVehicles.get(ttcSecondaryArrayIndex);

            // get the current location coordinates
            JSONArray coordinates = ttcVehicle.getJSONArray("coordinates");
            LatLng location = new LatLng(coordinates.getDouble(1), coordinates.getDouble(0));

            // get current route name
            String route_name = ttcVehicle.getString("route_name");

            // get current date and time
            String dateTime = Helper.convertTimestampToString(ttcVehicle.getLong("GPStime"));

            // calculate the direction based on the heading
            String direction = Helper.calculateDirection(Integer.parseInt(ttcVehicle.getString("heading")));

            // get the vehicle id and store it in inverted index table and add it to ttcMarker array
            int vehicle_id = ttcVehicle.getInt("vehicle_id");
            ttcInvertedIndex.put(vehicle_id, ttcMarkerIndex);
            ttcMarkers.add(mMap.addMarker(new MarkerOptions()
                    .position(location)
                    .icon(BitmapDescriptorFactory.fromBitmap(ttcIcon))
                    .title(route_name)
                    .snippet("Bus ID: " + vehicle_id + '\n' + "Direction: " + direction + '\n' + "Time: " + dateTime)
                    .visible(ttcIsChecked)));

            final int ttcMarkerIndexFinal = ttcMarkerIndex + 1;
            final int ttcSecondaryArrayIndexFinal = ttcSecondaryArrayIndex + 1;

            Handler ttcHandler = new Handler(Looper.getMainLooper());
            ttcHandler.post(new Runnable() {
                @Override
                public void run() {
                    ttcPlotFurtherRecursive(ttcVehicles, ttcMarkerIndexFinal, ttcSecondaryArrayIndexFinal);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void WebSocketConn() {
        System.out.println("Starting");

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
                        try {
                            final JSONObject ttcVehicle = new JSONObject(s);
                            handler.post(new WebsocketPlotTtcRunnable(ttcVehicle, ttcInvertedIndex, ttcMarkers, mMap, ttcIsChecked, ttcIcon));
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

    // Use online geocode api to search for coordinate of an address
    public void onSearch(View view) {
        EditText location = (EditText) getView().findViewById(R.id.home_search_input);
        String inputLocation = location.getText().toString();

        String url = "http://maps.googleapis.com/maps/api/geocode/json?address="+inputLocation.replace(" ", "%20");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject intersectionInfo) {
                        try {
                            double lat = intersectionInfo.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                            double lng = intersectionInfo.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                            LatLng latlng = new LatLng(lat, lng);
                            mMap.animateCamera(CameraUpdateFactory.newLatLng(latlng));
                            if (previousSearchedMarker == null) {
                                previousSearchedMarker = mMap.addMarker(new MarkerOptions().position(latlng));
                            }
                            else {
                                previousSearchedMarker.remove();
                                previousSearchedMarker = mMap.addMarker(new MarkerOptions().position(latlng));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("error = " + error);
            }
        });
        NetworkManager.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {

        void setActionBar(Toolbar toolbar);
    }
}

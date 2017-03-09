package ca.cvst.gta;

import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewAreaBasedActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker mapLocation;
    private LatLng centre = null;
    private Marker previousMarker = null;
    private Circle previousCircle = null;
    private int radius = 1000;
    private LatLng southwestPoint;
    private LatLng northeastPoint;

    private Marker previousSouthwest = null;
    private Marker previousNortheast = null;

    private ArrayList<LatLngBounds> subscribedLocations;

    private final Map<String, LatLng> linkIdCoorMapping = new HashMap<>();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_area_based_subscription);

        // Toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Complete Button
        Button completeBtn = (Button)findViewById(R.id.new_area_based_subscription_complete);
        completeBtn.setEnabled(false);
        completeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("LINK_ID", (String) mapLocation.getTag());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        // Search Button
        Button searchBtn = (Button)findViewById(R.id.new_area_based_subscription_search);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSearch(v);
            }
        });

        Button increaseRadius = (Button)findViewById(R.id.new_area_based_subscription_increase_radius);
        increaseRadius.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (previousCircle != null) {
                    radius = radius + 50;
                    previousCircle.setRadius(radius);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Please select an area on the map", Toast.LENGTH_SHORT).show();
                }

            }
        });

        Button decreaseRadius = (Button)findViewById(R.id.new_area_based_subscription_decrease_radius);
        decreaseRadius.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (previousCircle != null) {
                    radius = radius - 50;
                    previousCircle.setRadius(radius);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Please select an area on the map", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button subscribeButton = (Button)findViewById(R.id.new_area_based_subscription_subscribe);
        subscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (centre != null) {
                    LatLngBounds bounds = toBounds(centre, radius);
                    System.out.println("radius: " + radius);
                    System.out.println("centre coordinates: " + centre);
                    northeastPoint = bounds.northeast;
                    southwestPoint = bounds.southwest;

                    subscribedLocations.add(bounds);
                    previousCircle = null;

                    Toast.makeText(getApplicationContext(), "Subscribed", Toast.LENGTH_SHORT).show();
                    // just to check if the algorithm works
                    previousSouthwest = mMap.addMarker(new MarkerOptions().position(northeastPoint));
                    previousNortheast = mMap.addMarker(new MarkerOptions().position(southwestPoint));
                }
                else {
                    Toast.makeText(getApplicationContext(), "Please select an area on the map", Toast.LENGTH_SHORT).show();
                }

            }
        });
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_new_area_based_subscription);
        mapFragment.getMapAsync(this);

    }

    // Use google map api to search for coordinate of an address
    public void onSearch(View view) {
        EditText location = (EditText)findViewById(R.id.new_area_based_subscription_address_input);
        String inputLocation = location.getText().toString();
        List<Address> addressList = null;
        if (location != null || location.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(inputLocation, 1);
                System.out.println("FOUND THIS " + addressList.toString());
            } catch (IOException e) {
                System.out.println("Exception while fetching geocode for searched location.");
            }
            Address address = addressList.get(0);
            LatLng latlng = new LatLng(address.getLatitude(), address.getLongitude());

            mMap.animateCamera(CameraUpdateFactory.newLatLng(latlng));
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        radius = 1000; //default value
        subscribedLocations = new ArrayList<>();
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                if (previousNortheast != null) {
                    previousNortheast.remove();
                    previousSouthwest.remove();
                }

                if (previousMarker != null) {
                    previousMarker.remove();
                }
                if (previousCircle != null) {
                    previousCircle.remove();
                }
                previousMarker = mMap.addMarker(new MarkerOptions().position(point));
                centre = point;
                previousCircle = mMap.addCircle(new CircleOptions()
                        .center(point)
                        .radius(radius)
                        .strokeColor(Color.RED)
                        .fillColor(Color.TRANSPARENT));

            }
        });
    }

    public LatLngBounds toBounds(LatLng center, double radius) {
        LatLng southwest = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 225);
        LatLng northeast = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 45);
        return new LatLngBounds(southwest, northeast);
    }
}
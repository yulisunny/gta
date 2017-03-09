package ca.cvst.gta;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewAreaBasedActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker mapLocation;
    private LatLng topLeftPoint;
    private LatLng botRightPoint;
    private LatLng markerPoint;
    private Marker previousMarker = null;
    private Circle previousCircle = null;
    private int radius = 1000;

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
                radius = radius + 50;
                previousCircle.setRadius(radius);
            }
        });

        Button decreaseRadius = (Button)findViewById(R.id.new_area_based_subscription_decrease_radius);
        decreaseRadius.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radius = radius - 50;
                previousCircle.setRadius(radius);
            }
        });

        Button subcribeButton = (Button)findViewById(R.id.new_area_based_subscription_subscribe);
        subcribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(markerPoint);
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
        radius = 1000;
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                if (previousMarker != null) {
                    previousMarker.remove();
                }
                if (previousCircle != null) {
                    previousCircle.remove();
                }
                previousMarker = mMap.addMarker(new MarkerOptions().position(point));
                markerPoint = point;
                previousCircle = mMap.addCircle(new CircleOptions()
                        .center(point)
                        .radius(radius)
                        .strokeColor(Color.RED)
                        .fillColor(Color.TRANSPARENT));

            }
        });
    }


}
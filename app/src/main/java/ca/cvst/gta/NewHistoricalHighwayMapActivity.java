package ca.cvst.gta;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.github.mikephil.charting.data.Entry;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterItem;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewHistoricalHighwayMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker mapLocation;

    private final Map<String, LatLng> linkIdCoorMapping = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_historical_chart_map);

        // Toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Complete Button
        Button completeBtn = (Button)findViewById(R.id.btn_map_new_historical_chart_complete);
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
        Button searchBtn = (Button)findViewById(R.id.btn_map_new_historical_chart_search);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSearch(v);
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_new_historical_chart);
        mapFragment.getMapAsync(this);

        getLinkIdInfo();
    }

    // Use google map api to search for coordinate of an address
    public void onSearch(View view) {
        EditText location = (EditText)findViewById(R.id.historical_map_address_input);
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


            List<String> closestLinkIds = getClosestLinkIds(latlng);
            for(String linkId:closestLinkIds) {
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(linkIdCoorMapping.get(linkId)));
                marker.setTag(linkId);
            }
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latlng));
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker clickedMarker) {
                // Reset last button
                if (mapLocation != null && !clickedMarker.equals(mapLocation)) {
                    mapLocation.setIcon(BitmapDescriptorFactory.defaultMarker());
                    mapLocation.setZIndex(0f);
                }

                // Update marker color
                clickedMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                clickedMarker.setZIndex(1.0f);
                mapLocation = clickedMarker;

                // Enable completion button
                Button completeBtn = (Button)findViewById(R.id.btn_map_new_historical_chart_complete);
                completeBtn.setEnabled(true);
                return false;
            }
        });

//        // Find closest links
//        getClosestLinkIds()

//        // Place markers
//        for (Map.Entry<String, LatLng> entry : linkIdCoorMapping.entrySet()) {
//            Marker marker = googleMap.addMarker(new MarkerOptions()
//                    .position(entry.getValue()));
//            marker.setTag(entry.getKey());
//        }

    }

    // TomTomAPI
    private void getLinkIdInfo() {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(getAssets().open("linkIds.txt")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            String line;
            while ((line = br.readLine()) != null) {
                String[] linkInfo = line.split(",");
                linkIdCoorMapping.put(linkInfo[0], new LatLng(Double.valueOf(linkInfo[1]), Double.valueOf(linkInfo[2])));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private List<String> getClosestLinkIds(LatLng desiredLocation) {
        List<java.util.Map.Entry<String, Double>> pairList= new ArrayList<>();
        Double x = desiredLocation.latitude;
        Double y = desiredLocation.longitude;

        for(Map.Entry<String, LatLng> entry:linkIdCoorMapping.entrySet()) {
            LatLng currPoint = entry.getValue();
            Double distance = Math.pow(currPoint.latitude - x, 2) + Math.pow(currPoint.longitude - y, 2);
            pairList.add(new AbstractMap.SimpleEntry<>(entry.getKey(), distance));
        }

        Collections.sort(pairList, new LinkComparator());

        List<String> ret = new ArrayList<>();
        for (int i = 0; i < 10; i ++) {
            ret.add(pairList.get(i).getKey());
        }
        return ret;
    }

    public class LinkComparator implements Comparator<Map.Entry<String, Double>> {

        @Override
        public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
            return o1.getValue().compareTo(o2.getValue());
        }
    }
}

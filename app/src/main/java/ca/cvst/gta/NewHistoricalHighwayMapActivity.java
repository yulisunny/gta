package ca.cvst.gta;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

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
import java.util.Date;
import java.util.HashMap;
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

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_new_historical_chart);
        mapFragment.getMapAsync(this);

        getLinkIdInfo();
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
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

        // Place markers
        for (Map.Entry<String, LatLng> entry : linkIdCoorMapping.entrySet()) {
            Marker marker = googleMap.addMarker(new MarkerOptions()
                    .position(entry.getValue()));
            marker.setTag(entry.getKey());
        }

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

    public class LinkItem implements ClusterItem {
        private final LatLng mPosition;

        public LinkItem(double lat, double lng) {
            mPosition = new LatLng(lat, lng);
        }

        @Override
        public LatLng getPosition() {
            return mPosition;
        }
    }
}

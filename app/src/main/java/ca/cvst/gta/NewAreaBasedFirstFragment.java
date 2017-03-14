package ca.cvst.gta;

import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.SphericalUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by harryyu on 2017-03-13.
 */

public class NewAreaBasedFirstFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener {

    private MapView mMapView;
    private GoogleMap mMap;
    private LatLng centre = null;
    private Marker previousMarker = null;
    private Circle previousCircle = null;
    private int radius = 1000;
    private LatLng southwestPoint;
    private LatLng northeastPoint;
    private Marker previousSouthwest = null;
    private Marker previousNortheast = null;
    private ArrayList<LatLngBounds> subscribedLocations;
    private View root;
    private Polygon previousSquare;

    public NewAreaBasedFirstFragment() {
        // Rquried empty public constructor
    }

    public static NewAreaBasedFirstFragment newInstance() {
        NewAreaBasedFirstFragment fragment = new NewAreaBasedFirstFragment();
        return fragment;
    }

    public class FourCorners {
        public LatLng topLeft;
        public LatLng topRight;
        public LatLng botLeft;
        public LatLng botRight;

        public FourCorners(LatLng pTopLeft, LatLng pTopRight, LatLng pBotLeft, LatLng pBotRight) {
            topLeft = pTopLeft;
            topRight = pTopRight;
            botLeft = pBotLeft;
            botRight = pBotRight;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_area_based_subscriptions_first_page, container, false);

        //Toolbar toolbar = (Toolbar) root.findViewById(R.id.toolbar);
        // Search Button
        Button searchBtn = (Button) root.findViewById(R.id.new_area_based_subscription_search);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSearch(v);
            }
        });

        Button increaseRadius = (Button) root.findViewById(R.id.new_area_based_subscription_increase_radius);
        increaseRadius.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (previousSquare != null) {
                    radius = radius + 50;
                    FourCorners corners = toBounds(centre, radius);
                    ArrayList<LatLng> points = new ArrayList<>();
                    points.add(corners.topLeft);
                    points.add(corners.topRight);
                    points.add(corners.botRight);
                    points.add(corners.botLeft);
                    points.add(corners.topLeft);
                    previousSquare.setPoints(points);
//                    previousSquare.setPoints(corners.topLeft, corners.topRight, corners.botRight, corners.botLeft, corners.topLeft);
//                    previousCircle.setRadius(radius);
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Please select an area on the map", Toast.LENGTH_SHORT).show();
                }

            }
        });

        Button decreaseRadius = (Button) root.findViewById(R.id.new_area_based_subscription_decrease_radius);
        decreaseRadius.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (previousSquare != null) {
                    radius = radius - 50;
                    FourCorners corners = toBounds(centre, radius);
                    ArrayList<LatLng> points = new ArrayList<>();
                    points.add(corners.topLeft);
                    points.add(corners.topRight);
                    points.add(corners.botRight);
                    points.add(corners.botLeft);
                    points.add(corners.topLeft);
                    previousSquare.setPoints(points);
//                    previousCircle.setRadius(radius);
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Please select an area on the map", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button subscribeButton = (Button) root.findViewById(R.id.new_area_based_subscription_subscribe);
        subscribeButton.setOnClickListener(this);
        mMapView = (MapView) root.findViewById(R.id.map_new_area_based_subscription);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        return root;

        //return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
//        mGoogleApiClient.connect();
        mMapView.onStart();
        super.onStart();
    }

    @Override
    public void onStop() {
//        mGoogleApiClient.disconnect();
        mMapView.onStop();
        super.onStop();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof HomeMapFragment.OnFragmentInteractionListener) {
//            mListener = (HomeMapFragment.OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
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

    // Use google map api to search for coordinate of an address
    public void onSearch(View view) {
        EditText location = (EditText) root.findViewById(R.id.new_area_based_subscription_address_input);
        String inputLocation = location.getText().toString();
        List<Address> addressList = null;
        if (location != null || location.equals("")) {
            Geocoder geocoder = new Geocoder(getActivity().getApplicationContext());
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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        radius = 1000; //default value
        subscribedLocations = new ArrayList<>();
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
//                if (previousNortheast != null) {
//                    previousNortheast.remove();
//                    previousSouthwest.remove();
//                }

                if (previousMarker != null) {
                    previousMarker.remove();
                }
                if (previousSquare != null) {
                    previousSquare.remove();
                }
                previousMarker = mMap.addMarker(new MarkerOptions().position(point));
                centre = point;
//                previousCircle = mMap.addCircle(new CircleOptions()
//                        .center(point)
//                        .radius(radius)
//                        .strokeColor(Color.RED)
//                        .fillColor(Color.TRANSPARENT));

//                LatLngBounds corners = toBounds(point, radius);
//                LatLngBounds cornersopposite = toBoundsOpposite(point, radius);

                FourCorners corners = toBounds(point, radius);
                previousSquare = mMap.addPolygon(new PolygonOptions()
                        .add(corners.topLeft, corners.topRight, corners.botRight, corners.botLeft, corners.topLeft)
                        .strokeColor(Color.RED).fillColor(Color.TRANSPARENT));
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.new_area_based_subscription_subscribe:
                handleSubscribe();
        }
    }

    public FourCorners toBounds(LatLng center, double radius) {
        LatLng southwest = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 225);
        LatLng northeast = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 45);
        LatLng northwest = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 135);
        LatLng southeast = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 315);
//        return new LatLngBounds(southwest, northeast);
        return new FourCorners(northwest, northeast, southwest, southeast);
    }

    public LatLngBounds toBoundsCircle(LatLng center, double radius) {
        LatLng southwest = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 225);
        LatLng northeast = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 45);
        return new LatLngBounds(southwest, northeast);
    }

    private void handleSubscribe() {
        if (centre != null) {
            LatLngBounds bounds = toBoundsCircle(centre, radius);
//            System.out.println("radius: " + radius);
//            System.out.println("centre coordinates: " + centre);
//            northeastPoint = bounds.northeast;
//            southwestPoint = bounds.southwest;

            subscribedLocations.add(bounds);
//            previousCircle = null;
            previousSquare = null;
            Toast.makeText(getActivity().getApplicationContext(), "Subscribed", Toast.LENGTH_SHORT).show();
            // just to check if the algorithm works
//            previousSouthwest = mMap.addMarker(new MarkerOptions().position(northeastPoint));
//            previousNortheast = mMap.addMarker(new MarkerOptions().position(southwestPoint));
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "Please select an area on the map", Toast.LENGTH_SHORT).show();
        }
    }
}

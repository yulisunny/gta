package ca.cvst.gta;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NewIntersectionBasedFirstFragment extends Fragment implements OnMapReadyCallback {

    private MapView mMapView;
    private GoogleMap mMap;
    private Marker previousSearchedMarker = null;
    private ArrayList<LatLngBounds> subscribedLocations;
    private View root;
    private Polygon previousSquare = null;
    private LatLngBounds areaBounds = null;

    private OnFragmentInteractionListener mListener;

    public NewIntersectionBasedFirstFragment() {
        // Rquried empty public constructor
    }

    public static NewIntersectionBasedFirstFragment newInstance() {
        NewIntersectionBasedFirstFragment fragment = new NewIntersectionBasedFirstFragment();
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

        root = inflater.inflate(R.layout.fragment_intersection_based_subscriptions_first_page, container, false);

        Button searchBtn = (Button) root.findViewById(R.id.new_intersection_based_subscription_search);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSearch(v);
            }
        });

        Button subscribeButton = (Button) root.findViewById(R.id.new_intersection_based_subscription_subscribe);
        subscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (areaBounds != null) {
                    mListener.setCoordinates(areaBounds);
                    mListener.goToSecondSubscriptionPageFromFirstPage();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Please select an area on the map", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mMapView = (MapView) root.findViewById(R.id.map_new_intersection_based_subscription);
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
        if (context instanceof NewIntersectionBasedFirstFragment.OnFragmentInteractionListener) {
            mListener = (NewIntersectionBasedFirstFragment.OnFragmentInteractionListener) context;
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

    // Use online geocode api to search for coordinate of an address
    public void onSearch(View view) {
        EditText location = (EditText) root.findViewById(R.id.new_intersection_based_subscription_address_input);
        String inputLocation = location.getText().toString();

        String url = "http://maps.googleapis.com/maps/api/geocode/json?address="+inputLocation.replace(" ", "%20").replace("&", "AND");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject intersectionInfo) {
                        try {
                            double lat = intersectionInfo.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                            double lng = intersectionInfo.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lng");

                            String formatted_address = intersectionInfo.getJSONArray("results").getJSONObject(0).getString("formatted_address");
                            double upperLat = intersectionInfo.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("viewport").getJSONObject("northeast").getDouble("lat");
                            double lowerLat = intersectionInfo.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("viewport").getJSONObject("southwest").getDouble("lat");
                            double upperLng = intersectionInfo.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("viewport").getJSONObject("northeast").getDouble("lng");
                            double lowerLng = intersectionInfo.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("viewport").getJSONObject("southwest").getDouble("lng");

                            LatLng topRight = new LatLng(upperLat, upperLng); // northeast
                            LatLng topLeft = new LatLng(lowerLat, upperLng);
                            LatLng botLeft = new LatLng(lowerLat, lowerLng); // southwest
                            LatLng botRight = new LatLng(upperLat, lowerLng);
                            FourCorners corners = new FourCorners(topLeft, topRight, botLeft, botRight);

                            areaBounds = new LatLngBounds(botLeft, topRight);

                            if (previousSquare == null) {
                                previousSquare = mMap.addPolygon(new PolygonOptions()
                                        .add(corners.topLeft, corners.topRight, corners.botRight, corners.botLeft, corners.topLeft)
                                        .strokeColor(Color.RED).fillColor(Color.TRANSPARENT));
                            }
                            else {
                                previousSquare.remove();
                                previousSquare = mMap.addPolygon(new PolygonOptions()
                                        .add(corners.topLeft, corners.topRight, corners.botRight, corners.botLeft, corners.topLeft)
                                        .strokeColor(Color.RED).fillColor(Color.TRANSPARENT));
                            }


                            LatLng intersection = new LatLng(lat, lng);
                            mMap.animateCamera(CameraUpdateFactory.newLatLng(intersection));
                            if (previousSearchedMarker == null) {
                                previousSearchedMarker = mMap.addMarker(new MarkerOptions().position(intersection));
                            }
                            else {
                                previousSearchedMarker.remove();
                                previousSearchedMarker = mMap.addMarker(new MarkerOptions().position(intersection));
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
        NetworkManager.getInstance(getActivity().getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }


    public interface OnFragmentInteractionListener {
        void setCoordinates(LatLngBounds bounds);

        void goToSecondSubscriptionPageFromFirstPage();
    }

}

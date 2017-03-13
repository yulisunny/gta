package ca.cvst.gta;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import static com.google.android.gms.maps.CameraUpdateFactory.newLatLng;

public class PastNotificationFragment extends Fragment implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks {

    private OnFragmentInteractionListener mListener;

    private MapView mMapView;
    private GoogleMap mMap;
    private Marker marker;

    private RecyclerView mPastNotificationsRecyclerView;
    private RecyclerView.LayoutManager mPastNotificationListLayoutManager;
    private List<PastNotification> mPastNotifications;
    private PastNotificationListAdapter mPastNotificationsAdapter;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    public PastNotificationFragment() {
        // Required empty public constructor
    }


    public static PastNotificationFragment newInstance() {
        PastNotificationFragment fragment = new PastNotificationFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

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
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_past_notification, container, false);

        Toolbar toolbar = (Toolbar) root.findViewById(R.id.toolbar);
        mListener.setActionBar(toolbar);

        mMapView = (MapView) root.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);
        //mMapView.onCreate(savedInstanceState);

        mPastNotificationsRecyclerView = (RecyclerView) root.findViewById(R.id.recycler_past_notification_list);
        mPastNotificationListLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mPastNotificationsRecyclerView.setLayoutManager(mPastNotificationListLayoutManager);
        mPastNotifications = PastNotification.loadNFromDb(getActivity().getApplicationContext(), 10);
        mPastNotificationsAdapter = new PastNotificationListAdapter(mPastNotifications);
        mPastNotificationsRecyclerView.setAdapter(mPastNotificationsAdapter);
        mPastNotificationsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int current_item = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                    PastNotification current_notif = mPastNotifications.get(current_item);
                    LatLng latLng = new LatLng(current_notif.getLatitude(), current_notif.getLongitude());
                    CameraUpdate update = CameraUpdateFactory.newLatLng(latLng);
                    mMap.animateCamera(update);
                    if (marker != null) {
                        marker.remove();
                    }
                    marker = mMap.addMarker(new MarkerOptions().position(latLng));
                    System.out.println("current_item = " + current_item);
                }
            }
        });
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(mPastNotificationsRecyclerView);

        return root;
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        System.out.println("on connected called");
        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            System.out.println("Have permission to access to location");
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                LatLng currentLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                mMap.moveCamera(newLatLng(currentLocation));
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.fragment_past_notificaton, menu);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

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
        super.onResume();
        mMapView.onResume();
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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

    }

    public interface OnFragmentInteractionListener {
        void setActionBar(Toolbar toolbar);
    }
}

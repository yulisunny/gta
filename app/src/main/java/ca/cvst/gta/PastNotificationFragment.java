package ca.cvst.gta;

import android.content.Context;
import android.content.pm.PackageManager;
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

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.util.ArrayList;
import java.util.List;

public class PastNotificationFragment extends Fragment implements
        OnMapReadyCallback {

    private OnFragmentInteractionListener mListener;

    private MapView mMapView;
    private GoogleMap mMap;

    private RecyclerView mPastNotificationsRecyclerView;
    private RecyclerView.LayoutManager mPastNotificationListLayoutManager;
    private List<PastNotification> mPastNotifications;
    private PastNotificationListAdapter mPastNotificationsAdapter;

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
        mMapView.onCreate(savedInstanceState);

        mPastNotificationsRecyclerView = (RecyclerView) root.findViewById(R.id.recycler_past_notification_list);
        mPastNotificationListLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mPastNotificationsRecyclerView.setLayoutManager(mPastNotificationListLayoutManager);
        mPastNotifications = new ArrayList<>();
        mPastNotifications.add(new PastNotification("TTC", "Route 501 arriving at Queen's Park"));
        mPastNotifications.add(new PastNotification("Highway Traffic", "Incident on DVP northbound"));
        mPastNotificationsAdapter = new PastNotificationListAdapter(mPastNotifications);
        mPastNotificationsRecyclerView.setAdapter(mPastNotificationsAdapter);
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(mPastNotificationsRecyclerView);

        return root;
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
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
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

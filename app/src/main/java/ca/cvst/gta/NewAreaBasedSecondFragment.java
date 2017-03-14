package ca.cvst.gta;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;


public class NewAreaBasedSecondFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private View root;

    public NewAreaBasedSecondFragment() {
        // Rquried empty public constructor
    }

    public static NewAreaBasedSecondFragment newInstance() {
        NewAreaBasedSecondFragment fragment = new NewAreaBasedSecondFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_area_based_subscriptions_second_page, container, false);

        ToggleButton toggleSunday = (ToggleButton) root.findViewById(R.id.Sunday);
        toggleSunday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // toggle enabled
                } else {
                    // toggle disabled
                }
            }
        });

        ToggleButton toggleMonday = (ToggleButton) root.findViewById(R.id.Monday);
        toggleMonday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // toggle enabled
                } else {
                    // toggle disabled
                }
            }
        });

        ToggleButton toggleTuesday = (ToggleButton) root.findViewById(R.id.Tuesday);
        toggleTuesday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // toggle enabled
                } else {
                    // toggle disabled
                }
            }
        });

        ToggleButton toggleWednesday = (ToggleButton) root.findViewById(R.id.Wednesday);
        toggleWednesday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // toggle enabled
                } else {
                    // toggle disabled
                }
            }
        });

        ToggleButton toggleThursday = (ToggleButton) root.findViewById(R.id.Thursday);
        toggleThursday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // toggle enabled
                } else {
                    // toggle disabled
                }
            }
        });

        ToggleButton toggleFriday = (ToggleButton) root.findViewById(R.id.Friday);
        toggleFriday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // toggle enabled
                } else {
                    // toggle disabled
                }
            }
        });

        ToggleButton toggleSaturday = (ToggleButton) root.findViewById(R.id.Saturday);
        toggleSaturday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // toggle enabled
                } else {
                    // toggle disabled
                }
            }
        });

        //Toolbar toolbar = (Toolbar) root.findViewById(R.id.toolbar);
        // Search Button
//        Button searchBtn = (Button) root.findViewById(R.id.new_area_based_subscription_search);
//        searchBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onSearch(v);
//            }
//        });
//
//        Button increaseRadius = (Button) root.findViewById(R.id.new_area_based_subscription_increase_radius);
//        increaseRadius.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (previousSquare != null) {
//                    radius = radius + 50;
//                    FourCorners corners = toBounds(centre, radius);
//                    ArrayList<LatLng> points = new ArrayList<>();
//                    points.add(corners.topLeft);
//                    points.add(corners.topRight);
//                    points.add(corners.botRight);
//                    points.add(corners.botLeft);
//                    points.add(corners.topLeft);
//                    previousSquare.setPoints(points);
////                    previousSquare.setPoints(corners.topLeft, corners.topRight, corners.botRight, corners.botLeft, corners.topLeft);
////                    previousCircle.setRadius(radius);
//                } else {
//                    Toast.makeText(getActivity().getApplicationContext(), "Please select an area on the map", Toast.LENGTH_SHORT).show();
//                }
//
//            }
//        });
//
//        Button decreaseRadius = (Button) root.findViewById(R.id.new_area_based_subscription_decrease_radius);
//        decreaseRadius.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (previousSquare != null) {
//                    radius = radius - 50;
//                    FourCorners corners = toBounds(centre, radius);
//                    ArrayList<LatLng> points = new ArrayList<>();
//                    points.add(corners.topLeft);
//                    points.add(corners.topRight);
//                    points.add(corners.botRight);
//                    points.add(corners.botLeft);
//                    points.add(corners.topLeft);
//                    previousSquare.setPoints(points);
////                    previousCircle.setRadius(radius);
//                } else {
//                    Toast.makeText(getActivity().getApplicationContext(), "Please select an area on the map", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//        Button subscribeButton = (Button) root.findViewById(R.id.new_area_based_subscription_subscribe);
//        subscribeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (areaBounds != null) {
//                    mListener.setCoordinates(areaBounds);
//                    mListener.nextPage();
//                } else {
//                    Toast.makeText(getActivity().getApplicationContext(), "Please select an area on the map", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//        mMapView = (MapView) root.findViewById(R.id.map_new_area_based_subscription);
//        mMapView.onCreate(savedInstanceState);
//        mMapView.getMapAsync(this);

        return root;

        //return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NewAreaBasedSecondFragment.OnFragmentInteractionListener) {
            mListener = (NewAreaBasedSecondFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }


    public interface OnFragmentInteractionListener {
        void nextPage();
    }

}
package ca.cvst.gta;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLngBounds;

public class NewAreaBasedMainActivity extends AppCompatActivity
        implements NewAreaBasedFirstFragment.OnFragmentInteractionListener, NewAreaBasedSecondFragment.OnFragmentInteractionListener {

    LatLngBounds AreaBounds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_based_subscription_main_page);

        NewAreaBasedFirstFragment firstFragment = NewAreaBasedFirstFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_area_based_content_container, firstFragment).commit();
    }

    @Override
    public void setCoordinates(LatLngBounds bounds) {
        AreaBounds = bounds;
    }

    @Override
    public void nextPage() {
        NewAreaBasedSecondFragment secondFragment = NewAreaBasedSecondFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_area_based_content_container, secondFragment).commit();
    }
}

package ca.cvst.gta;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by harryyu on 2017-03-13.
 */

public class NewAreaBasedMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_based_subscription_main_page);

        NewAreaBasedFirstFragment firstFragment = NewAreaBasedFirstFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_area_based_content_container, firstFragment).commit();
    }
}

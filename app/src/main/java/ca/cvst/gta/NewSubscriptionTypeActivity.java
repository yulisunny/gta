package ca.cvst.gta;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by harryyu on 2017-03-06.
 */

public class NewSubscriptionTypeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subscription_types);
    }

    public void areaBasedSubscription(View view) {
        Intent intent = new Intent(getApplicationContext(), NewAreaBasedActivity.class);
        startActivity(intent);
    }

    public void intersectionBasedSubscription(View view) {
        Intent intent = new Intent(getApplicationContext(), NewIntersectionBasedActivity.class);
        startActivity(intent);
    }

    public void routeBasedSubscription(View view) {
        Intent intent = new Intent(getApplicationContext(), NewSubscriptionActivity.class);
        startActivity(intent);
    }

}
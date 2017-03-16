package ca.cvst.gta;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class NewSubscriptionTypeActivity extends AppCompatActivity {

    private String subscriptionName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_subscription_types);

    }

    public void areaBasedSubscription(View view) {

        EditText subscriptionNameInput = (EditText) findViewById(R.id.subscription_name);
        subscriptionName = subscriptionNameInput.getText().toString();

        Intent intent = new Intent(getApplicationContext(), NewAreaBasedMainActivity.class);
        intent.putExtra("subscription_name", subscriptionName);
        startActivity(intent);
    }

    public void intersectionBasedSubscription(View view) {

        EditText subscriptionNameInput = (EditText) findViewById(R.id.subscription_name);
        subscriptionName = subscriptionNameInput.getText().toString();

        Intent intent = new Intent(getApplicationContext(), NewIntersectionBasedMainActivity.class);
        intent.putExtra("subscription_name", subscriptionName);
        startActivity(intent);
    }

//    public void routeBasedSubscription(View view) {
//        Intent intent = new Intent(getApplicationContext(), NewSubscriptionActivity.class);
//        startActivity(intent);
//    }

}
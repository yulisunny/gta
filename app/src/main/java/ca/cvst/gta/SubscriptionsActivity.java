package ca.cvst.gta;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.List;

public class SubscriptionsActivity extends AppCompatActivity {

    private RecyclerView mSubscriptionsRecycler;
    private RecyclerView.LayoutManager mSubscriptionsLayoutManager;
    private List<Subscription> mSubscriptions;
    private SubscriptionListAdapter mSubscriptionListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscriptions);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NewSubscriptionTypeActivity.class);
                startActivity(intent);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSubscriptionsRecycler = (RecyclerView) findViewById(R.id.recycler_subscriptions_list);
        mSubscriptionsLayoutManager = new LinearLayoutManager(this);
        mSubscriptionsRecycler.setLayoutManager(mSubscriptionsLayoutManager);
        mSubscriptions = Subscription.loadAll(getApplicationContext());
        mSubscriptionListAdapter = new SubscriptionListAdapter(mSubscriptions);
        mSubscriptionsRecycler.setAdapter(mSubscriptionListAdapter);
    }

}

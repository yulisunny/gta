package ca.cvst.gta;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import layout.UpdatesListenerIntentService;

public class MainActivity extends AppCompatActivity implements
        HomeMapFragment.OnFragmentInteractionListener,
        HistoricalDashboardFragment.OnFragmentInteractionListener,
        PastNotificationFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setDefaultTab(R.id.tab_home);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                if (tabId == R.id.tab_historical_data_dashboard) {
                    HistoricalDashboardFragment dashFragment = HistoricalDashboardFragment.newInstance("blah", "whatev");
                    getSupportFragmentManager().beginTransaction().replace(R.id.contentContainer, dashFragment).commit();
                } else if (tabId == R.id.tab_home) {
                    HomeMapFragment mapFragment = HomeMapFragment.newInstance("blah", "lori");
                    mapFragment.setRetainInstance(true);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.contentContainer, mapFragment).commit();
                } else {
                    PastNotificationFragment pastNotificationFragment = PastNotificationFragment.newInstance();
                    getSupportFragmentManager().beginTransaction().replace(R.id.contentContainer, pastNotificationFragment).commit();
                }
            }
        });

        listenForNotif(false);
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(), "Back Pressed", Toast.LENGTH_LONG).show();
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_launch_subcriptions) {
            Intent intent = new Intent(this, SubscriptionsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setActionBar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
    }

    private void listenForNotif(boolean listenForNotif) {
        if (listenForNotif) {
            Intent updatesIntent = new Intent(getApplicationContext(), UpdatesListenerIntentService.class);
            startService(updatesIntent);
        }
    }

}

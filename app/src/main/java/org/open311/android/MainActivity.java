package org.open311.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.mapbox.mapboxsdk.MapboxAccountManager;

import org.codeforamerica.open311.facade.data.City;
import org.codeforamerica.open311.facade.data.ServiceRequest;
import org.open311.android.adapters.ViewPagerAdapter;

import org.open311.android.fragments.CityFragment;
import org.open311.android.fragments.PolicyFragment;
import org.open311.android.fragments.ProfileFragment;
import org.open311.android.fragments.ReportFragment;
import org.open311.android.fragments.RequestsFragment;
import org.open311.android.helpers.Installation;

import static org.open311.android.helpers.Utils.*;
import io.tus.android.client.TusPreferencesURLStore;

public class MainActivity extends AppCompatActivity
        implements
        RequestsFragment.OnListFragmentInteractionListener,
        CityFragment.OnListFragmentInteractionListener,
        FragmentManager.OnBackStackChangedListener {
    private String installationId;
    private ReportFragment reportFragment;
    private CityFragment cityFragment;
    private static final String LOG_TAG = "MainActivity";

    protected SharedPreferences settings;

    public String getInstallationId() {
        return installationId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);

        settings = getSettings(this);
        installationId = Installation.id(this);
        setContentView(R.layout.activity_main);

        // Mapbox access token only needs to be configured once in your app
        MapboxAccountManager.start(this, getString(R.string.mapbox_api_key));


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportFragmentManager().addOnBackStackChangedListener(this);


        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        if (savedInstanceState != null) {
            Log.d(LOG_TAG, "Restoring reportFragment");
            // Restore the fragment's instance
            reportFragment = (ReportFragment) getSupportFragmentManager().getFragment(
                    savedInstanceState, "reportFragment");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem actionItem = menu.findItem(R.id.setting_general);
        actionItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                cityFragment = new CityFragment();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.cities_fragment, cityFragment)
                        .commit();
                View citiesView = findViewById(R.id.cities_fragment);
                assert citiesView != null;
                citiesView.bringToFront();
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                FragmentManager fm = getSupportFragmentManager();
                if (fm.getBackStackEntryCount() > 0) {
                    fm.popBackStack();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onListFragmentInteraction(ServiceRequest item) {

    }

    @Override
    public void onBackStackChanged() {
        Log.d(LOG_TAG, "onBackStackChanged");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onSaveInstance");
        super.onSaveInstanceState(savedInstanceState);
        if (reportFragment != null) {
            // Save the fragment's instance
            Log.d(LOG_TAG, "Saving reportFragment");
            getSupportFragmentManager().putFragment(
                    savedInstanceState, "reportFragment", reportFragment);
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        Log.d(LOG_TAG, "onSupportNavigateUp");
        //This method is called when the up button is pressed. Just the pop back stack.
        getSupportFragmentManager().popBackStack();
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveSettings(this);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), MainActivity.this);
        adapter.addFragment(new ReportFragment());
        adapter.addFragment(new RequestsFragment());
        adapter.addFragment(new ProfileFragment());
        adapter.addFragment(new PolicyFragment());
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(LOG_TAG, "onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onListFragmentInteraction(City item) {

    }
}

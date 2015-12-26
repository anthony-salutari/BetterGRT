package com.asal.bettergrt;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;

import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private SharedPreferences mPreferences;
    private ProgressDialog progressDialog;

    private static final String ROUTES_PATH = "routes.txt";
    private static final String STOP_TIMES_PATH = "stop_times.txt";
    private static final String STOPS_PATH = "stops.txt";
    private static final String TRIPS_PATH = "trips.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // check if first time running application with sharedpreferences
        mPreferences = getSharedPreferences("app_status", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPreferences.edit();

        if (mPreferences.getBoolean("first", true)) {
            // first time launching app, let's load all the data
            loadData();

            //editor.putBoolean("first", false).commit();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (id == R.id.nav_near_me) {
            Intent intent = new Intent(this, NearMe.class);
            startActivity(intent);
        } else if (id == R.id.nav_map) {

        } else if (id == R.id.nav_favourites) {
            //todo fix fragment transactions
            FavouritesFragment fragment = (FavouritesFragment) getSupportFragmentManager().findFragmentById(R.id.list);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragment.newInstance(1);

            fragmentTransaction.replace(R.id.contentMain, fragment);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_theme) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_about) {

        } else if (id == R.id.nav_donate) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadData() {
        final LoadData loadData = new LoadData();
        //final InputStreamReader stopTimesReader;
        //final InputStreamReader stopsReader;
        //final InputStreamReader tripsReader;

        try {
            // InputStreamReaders for each text file
            final InputStreamReader routesReader = new InputStreamReader(getAssets().open(ROUTES_PATH));
            final InputStreamReader stopTimesReader = new InputStreamReader(getAssets().open(STOP_TIMES_PATH));
            final InputStreamReader stopsReader = new InputStreamReader(getAssets().open(STOPS_PATH));
            final InputStreamReader tripsReader = new InputStreamReader(getAssets().open(TRIPS_PATH));
        }
        catch (Exception e) {
            // handle exception
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading Information");
        progressDialog.setMessage("Loading current bus information. Please wait");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setProgress(0);
        progressDialog.setMax(4);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    loadData.loadStopTimes(new InputStreamReader(getAssets().open(STOP_TIMES_PATH)));
                    progressDialog.incrementProgressBy(1);

                    progressDialog.dismiss();
                } catch (Exception e) {
                    // handle exception
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    loadData.loadBusTrips(new InputStreamReader(getAssets().open(TRIPS_PATH)));
                } catch (Exception e) {
                    // handle exception
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    loadData.loadBusRoutes(new InputStreamReader(getAssets().open(ROUTES_PATH)));
                } catch (Exception e) {
                    // handle exception
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    loadData.loadBusStops(new InputStreamReader(getAssets().open(STOPS_PATH)));
                } catch (Exception e) {
                    // handle exception
                }
            }
        }).start();
    }
}


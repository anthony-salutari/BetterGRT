package com.asal.bettergrt;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.asal.bettergrt.dummy.DummyContent;

import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        FavouritesFragment.OnListFragmentInteractionListener,
        AboutFragment.OnFragmentInteractionListener,
        NearMe.OnListFragmentInteractionListener {

    private SharedPreferences mPreferences;
    private ProgressDialog progressDialog;
    private CoordinatorLayout coordinatorLayout;

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

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // load default section
        navigationView.setCheckedItem(R.id.nav_near_me);
        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            NearMe nearMe = new NearMe();
            fragmentTransaction.replace(R.id.frameLayout, nearMe);
            fragmentTransaction.commit();

/*            NearMe fragment = (NearMe) getSupportFragmentManager().findFragmentById(R.id.nearMeLayout);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragment = NearMe.newInstance();
            fragmentTransaction.replace(R.id.frameLayout, fragment);
            fragmentTransaction.commit();*/
        }

        // check if first time running application with sharedpreferences
        mPreferences = getSharedPreferences("app_status", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPreferences.edit();
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

        if (id == R.id.action_search) {
            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Search coming soon", Snackbar.LENGTH_LONG);
            snackbar.show();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_near_me) {
            NearMe fragment = (NearMe) getSupportFragmentManager().findFragmentById(R.id.nearMeLayout);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragment = NearMe.newInstance();
            fragmentTransaction.replace(R.id.frameLayout, fragment);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_map) {
            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Map coming soon", Snackbar.LENGTH_LONG);
            snackbar.show();
        } else if (id == R.id.nav_favourites) {
            FavouritesFragment fragment = (FavouritesFragment) getSupportFragmentManager().findFragmentById(R.id.favFrag);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragment = FavouritesFragment.newInstance(1);
            fragmentTransaction.replace(R.id.frameLayout, fragment);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_theme) {
            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Themes coming soon", Snackbar.LENGTH_LONG);
            snackbar.show();
        } else if (id == R.id.nav_share) {
            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Share coming soon", Snackbar.LENGTH_LONG);
            snackbar.show();
        } else if (id == R.id.nav_about) {
            AboutFragment fragment = (AboutFragment) getSupportFragmentManager().findFragmentById(R.id.aboutLayout);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragment = AboutFragment.newInstance("dkfj", "lakdjf");
            fragmentTransaction.replace(R.id.frameLayout, fragment);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_donate) {
            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Donate coming soon", Snackbar.LENGTH_LONG);
            snackbar.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onListFragmentInteraction(StopTime item) {

    }
}
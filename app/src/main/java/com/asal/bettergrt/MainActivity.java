package com.asal.bettergrt;

import android.app.SearchableInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import java.lang.reflect.Method;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        FavouritesFragment.OnListFragmentInteractionListener,
        AboutFragment.OnFragmentInteractionListener,
        Map.OnListFragmentInteractionListener {

    private SharedPreferences mPreferences;
    //private CoordinatorLayout coordinatorLayout;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.coordinatorLayout) CoordinatorLayout coordinatorLayout;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.nav_view) NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // check if first time running app to set some values
        mPreferences = getSharedPreferences("app_status", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPreferences.edit();
        if (mPreferences.getBoolean("first", true)) {
            Utilities.mTheme = Utilities.BLUE;
            editor.putBoolean("first", false).apply();
        }
        else {
            Utilities.mTheme = mPreferences.getInt("theme", Utilities.BLUE);
            Utilities.mMarkerHue = mPreferences.getFloat("markerHue", 207);
            Utilities.mSelectedMarkerHue = mPreferences.getFloat("selectedMarkerHue", 27);
        }

        Utilities.onCreateChangeTheme(this, mPreferences);

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        // load default section
        navigationView.setCheckedItem(R.id.nav_map);
        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Map map = new Map();
            fragmentTransaction.replace(R.id.frameLayout, map);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onBackPressed() {
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
            Intent searchIntent = new Intent(this, SearchActivity.class);
            startActivity(searchIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (id == R.id.nav_map) {
            Map map = new Map();
            fragmentTransaction.replace(R.id.frameLayout, map);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_favourites) {
            FavouritesFragment favouritesFragment = new FavouritesFragment();
            fragmentTransaction.replace(R.id.frameLayout, favouritesFragment);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_theme) {
            final CharSequence[] items = { "Red", "Pink", "Purple", "Deep Purple", "Indigo",
                    "Blue (Default)", "Light Blue", "Cyan", "Teal", "Green",
                    "Light Green", "Lime", "Yellow", "Amber", "Orange",
                    "Deep Orange", "Brown", "Grey", "Blue Grey" };

            int currentTheme = mPreferences.getInt("theme", Utilities.BLUE);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Select Theme");
            builder.setSingleChoiceItems(items, currentTheme, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Utilities.changeTheme(MainActivity.this, which, mPreferences);
                    }
                });

            AlertDialog alert = builder.create();
            alert.show();
        } else if (id == R.id.nav_about) {
            AboutFragment aboutFragment = new AboutFragment();
            fragmentTransaction.replace(R.id.frameLayout, aboutFragment);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_donate) {
            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Donate coming soon", Snackbar.LENGTH_LONG);
            snackbar.show();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onListFragmentInteraction(StopTime item) {

    }

    @Override
    public void onListFragmentInteraction(FavouriteStop item) {

    }
}
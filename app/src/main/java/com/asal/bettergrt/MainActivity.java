package com.asal.bettergrt;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.asal.bettergrt.dummy.DummyContent;

import java.io.InputStreamReader;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        FavouritesFragment.OnListFragmentInteractionListener,
        AboutFragment.OnFragmentInteractionListener,
        NearMe.OnListFragmentInteractionListener {

    private SharedPreferences mPreferences;
    private CoordinatorLayout coordinatorLayout;

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
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (id == R.id.nav_near_me) {
            NearMe nearMe = new NearMe();
            fragmentTransaction.replace(R.id.frameLayout, nearMe);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_map) {
            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Map coming soon", Snackbar.LENGTH_LONG);
            snackbar.show();
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

            AlertDialog.Builder builder = new AlertDialog.Builder(this, getThemeId());

            builder.setTitle("Select Theme");
            builder.setSingleChoiceItems(items, currentTheme, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Utilities.changeTheme(MainActivity.this, which, mPreferences);
                    }
                });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
        } else if (id == R.id.nav_share) {
            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Share coming soon", Snackbar.LENGTH_LONG);
            snackbar.show();
        } else if (id == R.id.nav_about) {
            AboutFragment aboutFragment = new AboutFragment();
            fragmentTransaction.replace(R.id.frameLayout, aboutFragment);
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

    int getThemeId() {
        try {
            Class<?> wrapper = Context.class;
            Method method = wrapper.getMethod("getThemeResId");
            method.setAccessible(true);
            return (Integer) method.invoke(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
package com.asal.bettergrt;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.lapism.searchview.SearchAdapter;
import com.lapism.searchview.SearchHistoryTable;
import com.lapism.searchview.SearchItem;
import com.lapism.searchview.SearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ArrayList<BusStop> searchResults;
    private SearchStopsAdapter recyclerViewAdapter;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.container) ViewPager viewPager;
    @BindView(R.id.tabs) TabLayout tabLayout;
    @BindView(R.id.searchView) SearchView searchView;
    //@BindView(R.id.search_stops_list) RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences mPreferences = getSharedPreferences("app_status", Context.MODE_PRIVATE);
        Utilities.mTheme = mPreferences.getInt("theme", Utilities.BLUE);
        Utilities.onCreateChangeTheme(this, mPreferences);

        setContentView(R.layout.activity_search);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        setTitle(null);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        viewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);

        setSearchView();

        searchResults = new ArrayList<>();

        setupRecyclerView();
    }

    private void setSearchView() {
        if (searchView != null) {
            // customize the searchview
            searchView.setVersion(SearchView.VERSION_TOOLBAR);
            searchView.setVersionMargins(SearchView.VERSION_MARGINS_TOOLBAR_BIG);
            searchView.setDivider(true);
            searchView.setAnimationDuration(SearchView.ANIMATION_DURATION);
            searchView.setHint("Search");
            searchView.setOnMenuClickListener(new SearchView.OnMenuClickListener() {
                @Override
                public void onMenuClick() {
                    if (searchView.isSearchOpen()) {
                        searchView.close(true);
                    } else {
                        finish();
                    }
                }
            });

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }

                @Override
                public boolean onQueryTextSubmit(String query) {
                    try {
                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder()
                                .url(getString(R.string.web_service_url) + "/searchBusStops?term=" + query)
                                .build();

                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {

                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                if (response.isSuccessful()) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response.body().string());
                                        JSONArray jsonArray = jsonObject.getJSONArray("SearchResults");

                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject busStopJson = jsonArray.getJSONObject(i);
                                            BusStop busStop = new BusStop();
                                            busStop.stopID = busStopJson.getString("stop_id");
                                            busStop.stopName = busStopJson.getString("stop_name").replace("\"", "");
                                            busStop.stopLat = Double.parseDouble(busStopJson.getString("stop_lat"));
                                            busStop.stopLon = Double.parseDouble(busStopJson.getString("stop_lon"));
                                            busStop.parentStation = busStopJson.getString("parent_station");
                                            busStop.location = new LatLng(busStop.stopLat, busStop.stopLon);

                                            searchResults.add(busStop);
                                        }


                                    } catch (JSONException e) {
                                        // handle JSONException
                                    }
                                }
                            }
                        });
                    } catch (Exception e) {
                        // handle exception
                    }

                    return false;
                }
            });
            searchView.setOnOpenCloseListener(new SearchView.OnOpenCloseListener() {
                @Override
                public void onClose() {

                }

                @Override
                public void onOpen() {

                }
            });

            List<SearchItem> suggestionsList = new ArrayList<>();
            suggestionsList.add(new SearchItem("search1"));
            suggestionsList.add(new SearchItem("search2"));
            suggestionsList.add(new SearchItem("search3"));

            SearchAdapter searchAdapter = new SearchAdapter(this, suggestionsList);
            searchAdapter.addOnItemClickListener(new SearchAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    searchView.close(true);
                }
            });

            searchView.setAdapter(searchAdapter);
        }
    }

    private void setupRecyclerView() {
        /*recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAdapter = new SearchStopsAdapter(searchResults);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setHasFixedSize(true);*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
/*        if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    public static class RecyclerViewFragment extends Fragment {
        // default constructor
        public RecyclerViewFragment() {

        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.search_stops_list, container, false);



            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return new RecyclerViewFragment();
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Bus Stops";
                case 1:
                    return "Routes";
            }
            return null;
        }
    }
}

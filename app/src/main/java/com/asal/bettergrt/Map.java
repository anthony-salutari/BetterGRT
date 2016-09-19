package com.asal.bettergrt;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Camera;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.MarkerManager;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Map extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnCameraChangeListener,
        GoogleMap.OnMarkerClickListener,
        android.location.LocationListener,
        SlidingUpPanelLayout.PanelSlideListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private ArrayList<BusStop> stops;
    private MarkerManager markerManager;
    private Marker mPrevMarker;
    private LocationManager mLocationManager;
    private RealtimeLocationHelper realtimeLocationHelper;
    public int mId;
    public Intent mRealtimeServiceIntent;

    private StopTimesAdapter mAdapter;

    private ArrayList<StopTime> mStopTimes;
    private OnListFragmentInteractionListener mListener;

    private final float anchorPoint = 0.3f;

    private static final int PERMISSION_FINE_LOCATION = 1;

    private CoordinatorLayout coordinatorLayout;

    @BindView(R.id.stopDetails) TextView stopDetails;
    @BindView(R.id.notificationButton) ImageButton notificationButton;
    @BindView(R.id.favouriteButton) ImageButton favouriteButton;
    @BindView(R.id.realtimeText) TextView realtimeText;
    @BindView(R.id.scrollableView) RecyclerView recyclerView;
    @BindView(R.id.sliding_layout) SlidingUpPanelLayout slidingLayout;
    private Unbinder unbinder;

    public static Map newInstance() {
        return new Map();
    }

    public Map() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivity().setTitle("Map");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.activity_map, container, false);

        unbinder = ButterKnife.bind(this, rootView);

        coordinatorLayout = (CoordinatorLayout) rootView.findViewById(R.id.coordinatorLayout);

        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();
        rootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        backPressed();
                        return true;
                    }
                }
                return false;
            }
        });

        // set the onclick methods for the buttons
        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNotification();
            }
        });
        favouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favouriteButtonClick();
            }
        });

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String time = intent.getStringExtra(RealtimeService.EXTRA_ACTUAL_TIME);
                if (time != null) {
                    realtimeText.setText(time);
                }
            }
        }, new IntentFilter(RealtimeService.ACTION_REALTIME_BROADCAST));

        mStopTimes = new ArrayList<>();

        // set up recyclerview
        setupRecyclerView();

        // // TODO: 9/16/2016 see if it's possible to butterknife the mapFragment
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        slidingLayout.setPanelState(PanelState.HIDDEN);

        realtimeLocationHelper = new RealtimeLocationHelper();

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
            }
        } else {
            mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1, this);
        }

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

/*        // todo set these values correctly
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(100000000);
        mLocationRequest.setFastestInterval(5000000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);*/

        // load the stops
        stops = new ArrayList<>();

        OkHttpClient okHttpClient = new OkHttpClient();

        Request request = new Request.Builder()
                .url(getString(R.string.web_service_url) + "/getStops")
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("BetterGRT", "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response.body().string());

                        JSONArray jsonArray = jsonResponse.getJSONArray("stops");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            BusStop busStop = new BusStop();
                            busStop.stopID = jsonObject.getString("stop_id");
                            busStop.stopName = jsonObject.getString("stop_name").replace("\"", "");
                            busStop.stopLat = Double.parseDouble(jsonObject.getString("stop_lat"));
                            busStop.stopLon = Double.parseDouble(jsonObject.getString("stop_lon"));
                            busStop.parentStation = jsonObject.getString("parent_station");
                            busStop.location = new LatLng(busStop.stopLat, busStop.stopLon);

                            if (busStop.parentStation.equals("")) {
                                stops.add(busStop);
                            }
                        }
                    } catch (IOException e) {
                        Log.d("BetterGRT", "onResponse IOException: " + e.getMessage());
                    } catch (org.json.JSONException e) {
                        Log.d("BetterGRT", "onResponse JSONException: " + e.getMessage());
                    } finally {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateMap();
                            }
                        });
                    }
                }
            }
        });

        //return super.onCreateView(inflater, container, savedInstanceState);

        return rootView;
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new StopTimesAdapter(mStopTimes, mListener);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setHasFixedSize(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (slidingLayout != null) {
            slidingLayout.setPanelState(PanelState.HIDDEN);
        }

        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    private void favouriteButtonClick() {
        Gson gson = new Gson();
        SharedPreferences favouritesPreference = getActivity().getSharedPreferences(Utilities.PREFERENCE_FAVOURITES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = favouritesPreference.edit();
        ArrayList<FavouriteStop> favouriteStops = new ArrayList<FavouriteStop>();

        String favouriteJson = favouritesPreference.getString("favourites", "");

        if (favouriteJson.isEmpty()) {
            favouriteStops = new ArrayList<>();
        } else {
            Type type = new TypeToken<List<FavouriteStop>>() {
            }.getType();

            favouriteStops = gson.fromJson(favouriteJson, type);
        }

        FavouriteStop favouriteStop = new FavouriteStop();
        String stopDetailsString = stopDetails.getText().toString();

        // split the stopDetailsString into stopID and stopName
        int space = stopDetailsString.indexOf(" ");
        favouriteStop.stopID = stopDetailsString.substring(0, space);
        favouriteStop.stopName = stopDetailsString.substring(space);

        favouriteStops.add(favouriteStop);

        String json = gson.toJson(favouriteStops);

        editor.putString("favourites", json);
        editor.apply();

        Toast.makeText(getActivity(), "Favourite Added", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (slidingLayout != null) {
            slidingLayout.setPanelState(PanelState.HIDDEN);
        }

        mListener = null;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (slidingLayout != null) {
            slidingLayout.setPanelState(PanelState.HIDDEN);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        try {
            mMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

        LatLng kitchenerLatlng = new LatLng(43.45, -80.483333);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(kitchenerLatlng));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(12));

        UiSettings settings = mMap.getUiSettings();
        settings.setMapToolbarEnabled(false);
        mMap.setOnCameraChangeListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (slidingLayout.getPanelState() != PanelState.HIDDEN) {
                    slidingLayout.setPanelState(PanelState.HIDDEN);
                }

                if (mPrevMarker != null) {
                    mPrevMarker.setIcon(BitmapDescriptorFactory.defaultMarker(Utilities.mMarkerHue));
                }
            }
        });

        markerManager = new MarkerManager(mMap);
        markerManager.newCollection("markerCollection");
    }

    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        //startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

/*    protected void startLocationUpdates() {
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        catch (SecurityException e) {
            Snackbar snackbar = Snackbar.make(getView(), "Error " + e.getMessage(), Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    } */

    @Override
    public void onLocationChanged(Location location) {
        try {
            mLastLocation = location;
            LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
            mLocationManager.removeUpdates(this);
        } catch (SecurityException e) {
            // handle exception
        }
        updateMap();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void updateMap() {
/*        LatLng location = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(18)); */

        if (stops.size() != 0) {
            for (BusStop stop : stops) {
                try {
                    markerManager.getCollection("markerCollection").addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(Utilities.mMarkerHue)).position(stop.location).title(stop.stopID + " " + stop.stopName));
                } catch (Exception e) {
                    Log.d("BetterGRT", e.getMessage());
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_FINE_LOCATION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //startLocationUpdates();
                try {
                    mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1, this);
                } catch (SecurityException e) {
                    Snackbar.make(coordinatorLayout, "Error accessing location", Snackbar.LENGTH_LONG).show();
                }
            } else {
                Snackbar.make(coordinatorLayout, "Location permission denied", Snackbar.LENGTH_LONG).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        Collection<Marker> markers =  markerManager.getCollection("markerCollection").getMarkers();
        Marker[] markerList = markers.toArray(new Marker[markers.size()]);

        for (int i = 0; i < markerList.length; i++) {
            if(i % 100 == 0) {
                markerList[i].setVisible(cameraPosition.zoom > 8);
            }
            else if (i % 10 == 0) {
                markerList[i].setVisible(cameraPosition.zoom > 12);
            }
            else {
                markerList[i].setVisible(cameraPosition.zoom > 14);
            }
        }
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        if (mPrevMarker != null) {
            mPrevMarker.setIcon(BitmapDescriptorFactory.defaultMarker(Utilities.mMarkerHue));
        }

        mPrevMarker = marker;
        stopDetails.setText(marker.getTitle());

        marker.hideInfoWindow();
        marker.setIcon(BitmapDescriptorFactory.defaultMarker(Utilities.mSelectedMarkerHue));
        if (mMap.getCameraPosition().zoom < 15) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 15), 1000, null);
        }
        else {
            mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()), 1000, null);
        }

        final String stopID = marker.getTitle().substring(0, marker.getTitle().indexOf(" "));

        mStopTimes.clear();

        OkHttpClient client = new OkHttpClient();
        Request request = null;

        // check if the stop is a terminal
        if (stopID.contains("place")) {
            request = new Request.Builder()
                    .url(getString(R.string.web_service_url) + "/getTerminal?stopID=" + stopID)
                    .build();
        } else {
            request = new Request.Builder()
                    .url(getString(R.string.web_service_url) + "/getStopTimes?stopID=" + stopID)
                    .build();
        }

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(getString(R.string.log_tag), "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response.body().string());

                        JSONArray jsonArray = jsonResponse.getJSONArray("route");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            StopTime stopTime = new StopTime();
                            stopTime.routeID = jsonObject.getString("route_id");
                            stopTime.tripHeadsign = jsonObject.getString("trip_headsign");
                            stopTime.departureTime = jsonObject.getString("departure_time");

                            mStopTimes.add(stopTime);
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                recyclerView.scrollToPosition(0);
                                mAdapter.addItems(mStopTimes);

                                // start the realtime service
                                mRealtimeServiceIntent = new Intent(getActivity(), RealtimeService.class);
                                mRealtimeServiceIntent.putExtra(RealtimeService.EXTRA_STOP_ID, stopID);
                                // TODO only starting the service on the first stoptime for testing purposes implement more robust functionality later
                                mRealtimeServiceIntent.putExtra(RealtimeService.EXTRA_ROUTE_ID, mStopTimes.get(0).routeID);
                                getActivity().startService(mRealtimeServiceIntent);
                            }
                        });
                    } catch (Exception e) {
                        // handle exception
                    }
                }
            }
        });

        if (slidingLayout.getPanelState() != PanelState.EXPANDED) {
            slidingLayout.setPanelState(PanelState.COLLAPSED);
        }
        else {
            slidingLayout.setPanelState(PanelState.EXPANDED);
        }

        return true;
    }

    private void backPressed() {
        if (mPrevMarker != null) {
            if (slidingLayout.getPanelState() == PanelState.EXPANDED) {
                slidingLayout.setPanelState(PanelState.COLLAPSED);
            }
            else if (slidingLayout.getPanelState() == PanelState.COLLAPSED) {
                mPrevMarker.setIcon(BitmapDescriptorFactory.defaultMarker(Utilities.mMarkerHue));
                slidingLayout.setPanelState(PanelState.HIDDEN);
            }
        }
    }

    private void runOnUiThread(Runnable task) {
        new Handler(Looper.getMainLooper()).post(task);
    }

    @Override
    public void onPanelSlide(View panel, float slideOffset) {

    }

    @Override
    public void onPanelStateChanged(View panel, PanelState previousState, PanelState newState) {
        mAdapter.notifyDataSetChanged();

        // check if panel is hidden and stop the realtime service
        if (newState == PanelState.HIDDEN) {
            if (mRealtimeServiceIntent != null) {
                getActivity().stopService(mRealtimeServiceIntent);
            }
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(StopTime item);
    }

    private void createNotification() {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getActivity())
                .setSmallIcon(R.drawable.ic_bus)
                .setContentTitle("BetterGRT")
                .setContentText("Next bus in -- minutes")
                .setAutoCancel(true);

        Intent resultIntent = new Intent(getActivity(), MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getActivity());
        stackBuilder.addParentStack(MainActivity.class);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        notificationBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(mId, notificationBuilder.build());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        unbinder.unbind();
    }
}

package com.asal.bettergrt;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterManager;
import com.opencsv.CSVReader;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;

import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class NearMe extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private ArrayList<String[]> stops;
    private ClusterManager<OldBusStop> stopsClusterManager;
    private SlidingUpPanelLayout mSlidingLayout;

    private TextView stopDetails;

    private static final int PERMISSION_FINE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_me);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        stopDetails = (TextView) findViewById(R.id.stopDetails);
        mSlidingLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);

        // hide the slide layout
        mSlidingLayout.setPanelState(PanelState.HIDDEN);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
            }
        }

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(100000000);
        mLocationRequest.setFastestInterval(5000000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // testing load data
/*        try {
            LoadData loadData = new LoadData();
            ArrayList<BusRoute> routes = new ArrayList<>();
            InputStreamReader routeStreamReader = new InputStreamReader(getAssets().open("routes.txt"));
            routes = loadData.loadBusRoutes(routeStreamReader);

            ArrayList<BusStop> busStops = new ArrayList<>();
            InputStreamReader stopsStreamReader = new InputStreamReader(getAssets().open("stops.txt"));
            busStops = loadData.loadBusStops(stopsStreamReader);

            ArrayList<BusTrip> busTrips = new ArrayList<>();
            InputStreamReader tripsStreamReader = new InputStreamReader(getAssets().open("trips.txt"));
            busTrips = loadData.loadBusTrips(tripsStreamReader);

            ArrayList<StopTime> stopTimes = new ArrayList<>();
            InputStreamReader timesStreamReader = new InputStreamReader(getAssets().open("stop_times.txt"));
            stopTimes = loadData.loadStopTimes(timesStreamReader);
        }
        catch (Exception e) {
            e.getMessage();
        }*/

        stops = new ArrayList<>();
        String next[];

        try {
            CSVReader reader = new CSVReader(new InputStreamReader(getAssets().open("stops.txt")));
            while (true) {
                next = reader.readNext();
                if (next != null) {
                    stops.add(next);
                }
                else {
                    break;
                }
            }
        } catch (Exception e) {
            // handle exception
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        UiSettings settings = mMap.getUiSettings();
        settings.setMapToolbarEnabled(false);
        stopsClusterManager = new ClusterManager<>(this, mMap);
        mMap.setOnCameraChangeListener(stopsClusterManager);
        mMap.setOnMarkerClickListener(stopsClusterManager);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (mSlidingLayout.getPanelState() != PanelState.HIDDEN) {
                    mSlidingLayout.setPanelState(PanelState.HIDDEN);
                }
            }
        });

        stopsClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<OldBusStop>() {
            @Override
            public boolean onClusterItemClick(OldBusStop busStop) {
                // stop chosen, show the slide layout and bus stop info
                mSlidingLayout.setPanelState(PanelState.COLLAPSED);
                stopDetails.setText(busStop.getId() + " " + busStop.name);
                return false;
            }
        });

        // Add a marker in Sydney and move the camera
/*        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        updateMap();
    }

    private void updateMap() {
        LatLng location = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(18));

        // add markers for each stop
        for (int i = 0; i < stops.size(); i++) {
            try {
                double latitude = Double.parseDouble(stops.get(i)[0]);
                double longitude = Double.parseDouble(stops.get(i)[2]);
                LatLng stopLocation = new LatLng(latitude, longitude);
                OldBusStop stop = new OldBusStop();
                stop.location = stopLocation;
                stop.id = Integer.parseInt(stops.get(i)[3]);
                stop.name = stops.get(i)[7];

                stopsClusterManager.addItem(stop);
                //mMap.addMarker(new MarkerOptions().position(stop.location).title(stop.id + " " + stop.name));

            } catch (Exception e) {
                // handle exception
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode) {
            case PERMISSION_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocationUpdates();
                }
                else {

                }
                return;
            }
        }
    }
}

package com.asal.bettergrt;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.MarkerManager;
import com.google.maps.android.clustering.ClusterManager;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NearMe extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnCameraChangeListener,
        GoogleMap.OnMarkerClickListener,
        LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private ArrayList<BusStop> stops;
    private ClusterManager<OldBusStop> stopsClusterManager;
    private SlidingUpPanelLayout mSlidingLayout;
    private MarkerManager markerManager;
    private Marker mPrevMarker;

    private TextView stopDetails;

    private static final int PERMISSION_FINE_LOCATION = 1;

    public static NearMe newInstance() {
        NearMe nearMe = new NearMe();

        return nearMe;
    }

    public NearMe() {
/*        okHttpClient = new OkHttpClient();
        stops = new ArrayList<>();

        Request request = new Request.Builder()
                .url(getString(R.string.web_service_url) + "/getStops")
                .build();*/

        /*okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(getActivity(), "Loading stops failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("BetterGRT", "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONArray jsonArray = new JSONArray(response.body().string());

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            BusStop busStop = new BusStop();
                            busStop.stopID = Integer.parseInt(jsonObject.getString("stop_id"));
                            busStop.stopName = jsonObject.getString("stop_name");
                            busStop.stopLat = Double.parseDouble(jsonObject.getString("stop_lat"));
                            busStop.stopLon = Double.parseDouble(jsonObject.getString("stop_lon"));
                            busStop.parentStation = jsonObject.getString("parent_station");
                            busStop.location = new LatLng(busStop.stopLat, busStop.stopLon);

                            stops.add(busStop);
                        }

                        updateMap();
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("BetterGRT", "onResponse: " + e.getMessage());
                    }
                }
            }
        });*/
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_near_me, container, false);

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

        getActivity().setTitle("Near Me");
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        stopDetails = (TextView) rootView.findViewById(R.id.stopDetails);
        mSlidingLayout = (SlidingUpPanelLayout) rootView.findViewById(R.id.sliding_layout);

        mSlidingLayout.setPanelState(PanelState.HIDDEN);

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
            }
        }

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        // todo set these values correctly
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(100000000);
        mLocationRequest.setFastestInterval(5000000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

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
                            busStop.stopID = Integer.parseInt(jsonObject.getString("stop_id"));
                            busStop.stopName = jsonObject.getString("stop_name").replace("\"", "");
                            busStop.stopLat = Double.parseDouble(jsonObject.getString("stop_lat"));
                            busStop.stopLon = Double.parseDouble(jsonObject.getString("stop_lon"));
                            busStop.parentStation = jsonObject.getString("parent_station");
                            busStop.location = new LatLng(busStop.stopLat, busStop.stopLon);

                            stops.add(busStop);
                        }
                    } catch (Exception e) {
                        Log.d("BetterGRT", "onResponse: " + e.getMessage());
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        try {
            mMap.setMyLocationEnabled(true);
        }
        catch (SecurityException e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

        UiSettings settings = mMap.getUiSettings();
        settings.setMapToolbarEnabled(false);
        mMap.setOnCameraChangeListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (mSlidingLayout.getPanelState() != PanelState.HIDDEN) {
                    mSlidingLayout.setPanelState(PanelState.HIDDEN);
                }

                if (mPrevMarker != null) {
                    mPrevMarker.setIcon(BitmapDescriptorFactory.defaultMarker());
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
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    protected void startLocationUpdates() {
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        catch (SecurityException e) {
            Snackbar snackbar = Snackbar.make(getView(), "Error " + e.getMessage(), Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        updateMap();
    }

    private void updateMap() {
/*        LatLng location = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(18));*/

        if (stops.size() != 0) {
            for (BusStop stop : stops) {
                try {
                    markerManager.getCollection("markerCollection").addMarker(new MarkerOptions().position(stop.location).title(stop.stopID + " " + stop.stopName));
                } catch (Exception e) {
                    Log.d("BetterGRT", e.getMessage());
                }
            }
        }

        // add markers for each stop
/*        for (int i = 0; i < stops.size(); i++) {
            try {

                double latitude = Double.parseDouble(stops.get(i)[0]);
                double longitude = Double.parseDouble(stops.get(i)[2]);
                LatLng stopLocation = new LatLng(latitude, longitude);
                OldBusStop stop = new OldBusStop();
                stop.location = stopLocation;
                stop.id = Integer.parseInt(stops.get(i)[3]);
                stop.name = stops.get(i)[7];

                markerManager.getCollection("markerCollection").addMarker(new MarkerOptions().position(stop.location).title(stop.id + " " + stop.name));

            } catch (Exception e) {
                Snackbar snackbar = Snackbar.make(getView(), "Error: " + e.getMessage(), Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        }*/
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
                    //todo handle denied location permission
                }
                return;
            }
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
    public boolean onMarkerClick(Marker marker) {
        if (mPrevMarker != null) {
            mPrevMarker.setIcon(BitmapDescriptorFactory.defaultMarker());
        }

        mPrevMarker = marker;

        if (mSlidingLayout.getPanelState() != PanelState.EXPANDED) {
            mSlidingLayout.setPanelState(PanelState.COLLAPSED);
        }
        else {
            mSlidingLayout.setPanelState(PanelState.EXPANDED);
        }

        marker.hideInfoWindow();
        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        if (mMap.getCameraPosition().zoom < 15) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 15), 1000, null);
        }
        else {
            mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()), 1000, null);
        }
        stopDetails.setText(marker.getTitle());
        return true;
    }

    private void backPressed() {
        if (mPrevMarker != null) {
            if (mSlidingLayout.getPanelState() == PanelState.EXPANDED) {
                mSlidingLayout.setPanelState(PanelState.COLLAPSED);
            }
            else if (mSlidingLayout.getPanelState() == PanelState.COLLAPSED) {
                mPrevMarker.setIcon(BitmapDescriptorFactory.defaultMarker());
                mSlidingLayout.setPanelState(PanelState.HIDDEN);
            }
        }
    }

    private void runOnUiThread(Runnable task) {
        new Handler(Looper.getMainLooper()).post(task);
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
}

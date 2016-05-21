package com.asal.bettergrt;

import android.util.Log;

import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;
import com.google.transit.realtime.GtfsRealtime.TripUpdate;

import java.net.URL;

/**
 * Created by Antonio on 2016-05-15.
 */
public class RealtimeLocationHelper {

    private static final String VECHICLE_POSITIONS_URL = "http://192.237.29.212:8080/gtfsrealtime/VehiclePositions";
    private static final String TRIP_UPDATES_URL = "http://192.237.29.212:8080/gtfsrealtime/TripUpdates";
    private static final String TAG = "BetterGRT";

    public void getTripUpdates() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(TRIP_UPDATES_URL);

                    FeedMessage feed = FeedMessage.parseFrom(url.openStream());

                    for (FeedEntity entity : feed.getEntityList()) {
                        if (entity.hasTripUpdate()) {
                            Log.d(TAG, "getBusLocations: ");
                        }
                    }

                } catch (Exception e) {
                    Log.d(TAG, "getBusLocations: " + e.getMessage());
                }
            }
        }).start();
    }
}

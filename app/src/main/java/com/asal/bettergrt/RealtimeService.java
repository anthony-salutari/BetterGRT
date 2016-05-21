package com.asal.bettergrt;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;

public class RealtimeService extends Service {
    private final String TRIP_UPDATES_URL = "http://192.237.29.212:8080/gtfsrealtime/TripUpdates";
    private final String VECHICLE_POSITIONS_URL = "http://192.237.29.212:8080/gtfsrealtime/VehiclePositions";

    public RealtimeService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {

            }
        }, 0, 30, TimeUnit.SECONDS);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void getTripUpdates(int stopID) {
        try {
            URL url = new URL(TRIP_UPDATES_URL);

            FeedMessage feed = FeedMessage.parseFrom(url.openStream());

            for (FeedEntity entity : feed.getEntityList()) {

            }

        } catch (Exception e) {

        }
    }
}

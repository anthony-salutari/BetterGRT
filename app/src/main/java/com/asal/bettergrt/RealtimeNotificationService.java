package com.asal.bettergrt;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;
import com.google.transit.realtime.GtfsRealtime.TripUpdate;
import com.google.transit.realtime.GtfsRealtime.TripUpdate.StopTimeUpdate;
import com.google.transit.realtime.GtfsRealtime.TripUpdate.StopTimeEvent;
import com.google.transit.realtime.GtfsRealtime.TripDescriptor;

public class RealtimeNotificationService extends Service {
    private final String TRIP_UPDATES_URL = "http://192.237.29.212:8080/gtfsrealtime/TripUpdates";
    private final String VECHICLE_POSITIONS_URL = "http://192.237.29.212:8080/gtfsrealtime/VehiclePositions";

    public RealtimeNotificationService() {
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
    public IBinder onBind (Intent intent){
        return null;
    }

    private void getTripUpdates(String stopID, String routeID) {
        try {
            URL url = new URL(TRIP_UPDATES_URL);

            FeedMessage feed = FeedMessage.parseFrom(url.openStream());

            for (FeedEntity entity : feed.getEntityList()) {
                // check if the entity has a trip update
                if (entity.hasTripUpdate()) {
                    TripUpdate tripUpdate = entity.getTripUpdate();

                    // get the trip
                    TripDescriptor trip = tripUpdate.getTrip();

                    // check if the trip matches the route
                    if (trip.getRouteId().equals(routeID)) {
                        // get the StopTimeUpdates
                        List<StopTimeUpdate> stopTimeUpdate = tripUpdate.getStopTimeUpdateList();

                        // iterate through the StopTimeUpdates
                        for (StopTimeUpdate update : stopTimeUpdate) {
                            if (update.getStopId().equals(stopID)) {
                                // found the correct stop
                                StopTimeEvent arrival = update.getArrival();
                                Long absoluteTime = arrival.getTime();

                                // convert from the supplied POSIX time
                                String formattedTime = convertAbsoluteTime(absoluteTime);
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            Log.d(getString(R.string.log_tag), e.getMessage());
        }
    }

    private String convertAbsoluteTime(Long time) {
        String formattedTime = null;
        // multiply the time by 1000 to get the time in milliseconds
        Long epoch = time * 1000;
        SimpleDateFormat formatter = new SimpleDateFormat("h:mm:ss a");
        formatter.setTimeZone(TimeZone.getTimeZone("America/Toronto"));

        try {
            Date date = new Date(epoch);
            formattedTime = formatter.format(date);
        } catch (Exception e) {
            Log.d(getString(R.string.log_tag), "convertAbsoluteTime: " + e.getMessage());
        }
        return formattedTime;
    }
}

package com.asal.bettergrt;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;
import com.google.transit.realtime.GtfsRealtime.TripUpdate;
import com.google.transit.realtime.GtfsRealtime.TripUpdate.StopTimeUpdate;
import com.google.transit.realtime.GtfsRealtime.TripUpdate.StopTimeEvent;
import com.google.transit.realtime.GtfsRealtime.TripDescriptor;

public class RealtimeService extends Service {
    private final String TRIP_UPDATES_URL = "http://192.237.29.212:8080/gtfsrealtime/TripUpdates";
    private final String VECHICLE_POSITIONS_URL = "http://192.237.29.212:8080/gtfsrealtime/VehiclePositions";
    public static final String ACTION_REALTIME_BROADCAST = RealtimeService.class.getName() + "RealtimeBroadcast";

    public static final String EXTRA_STOP_ID = "extra_stop_id";
    public static final String EXTRA_ROUTE_ID = "extra_route_id";
    public static final String EXTRA_ACTUAL_TIME = "extra_actual_time";

    public RealtimeService() {
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        android.os.Debug.waitForDebugger();
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                // get the stopID from the intent and pass it to getTripUpdates
                getTripUpdates(intent.getStringExtra(EXTRA_STOP_ID), intent.getStringExtra(EXTRA_ROUTE_ID));

                //testing
                //getTripUpdates("2677", 8);
            }
        }, 0, 30, TimeUnit.SECONDS);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
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

                                // send the broadcast with the properly formatted time
                                sendBroadcast(formattedTime);
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {

        }
    }

    private void sendBroadcast(String time) {
        if (time != null) {
            Intent intent = new Intent(ACTION_REALTIME_BROADCAST);
            intent.putExtra(EXTRA_ACTUAL_TIME, time);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }

    private String convertAbsoluteTime(Long time) {
        String formattedTime = null;

        try {
            Date unformatedTime = new Date(time);
            formattedTime = new SimpleDateFormat("h:mm:ss").format(unformatedTime);
        } catch (Exception e) {
            // handle exception
        }
        return formattedTime;
    }
}

package com.asal.bettergrt;

import com.google.android.gms.maps.model.LatLng;
import com.opencsv.CSVReader;

import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Anthony on 12/23/2015.
 */
public class LoadData {

    // static final strings to compare for the CSV reader to skip the first row
    private static final String BUS_ROUTE_CHECK = "route_long_name";
    private static final String BUS_STOP_CHECK = "stop_lat";
    private static final String BUS_TRIP_CHECK = "block_id";
    private static final String BUS_TIMES_CHECK = "trip_id";

    public ArrayList<BusRoute> loadBusRoutes(InputStreamReader inputStream) {
        ArrayList<BusRoute> routes = new ArrayList<>();
        String[] next;

        // read the routes.txt file and put into
        try {
            CSVReader reader = new CSVReader(inputStream);
            while (true) {
                next = reader.readNext();
                if (next != null) {
                    if (!next[0].equals(BUS_ROUTE_CHECK)) {
                        BusRoute route = new BusRoute();

                        route.routeLongName = next[0];
                        route.routeID = Integer.parseInt(next[1]);
                        route.routeType = Integer.parseInt(next[2]);
                        route.routeShortName = Integer.parseInt(next[8]);

                        routes.add(route);
                    }
                }
                else {
                    break;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        return routes;
    }

    public ArrayList<BusStop> loadBusStops(InputStreamReader inputStream) {
        ArrayList<BusStop> stops = new ArrayList<>();
        String[] next;

        try {
            CSVReader reader = new CSVReader(inputStream);
            while (true) {
                next = reader.readNext();
                if (next != null) {
                    if(!next[0].equals(BUS_STOP_CHECK)) {
                        BusStop stop = new BusStop();

                        stop.stopLat = Double.parseDouble(next[0]);
                        stop.stopLon = Double.parseDouble(next[2]);
                        stop.stopID = Integer.parseInt(next[3]);
                        stop.location = new LatLng(stop.stopLat, stop.stopLon);
                        stop.stopName = next[7];

                        stops.add(stop);
                    }
                }
                else {
                    break;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        return stops;
    }

    public ArrayList<BusTrip> loadBusTrips(InputStreamReader inputStream) {
        ArrayList<BusTrip> trips = new ArrayList<>();
        String[] next;

        try {
            CSVReader reader = new CSVReader(inputStream);
            while (true) {
                next = reader.readNext();
                if (next != null) {
                    if (!next[0].equals(BUS_TRIP_CHECK)) {
                        BusTrip trip = new BusTrip();

                        trip.blockID = next[0];
                        trip.routeID = Integer.parseInt(next[1]);
                        trip.directionID = Integer.parseInt(next[2]);
                        trip.tripHeadsign = next[3];
                        trip.shapeID = Integer.parseInt(next[4]);
                        trip.serviceID = next[5];
                        trip.tripID = Integer.parseInt(next[6]);

                        trips.add(trip);
                    }
                }
                else {
                    break;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        return trips;
    }

    public ArrayList<StopTime> loadStopTimes(InputStreamReader inputStream) {
        ArrayList<StopTime> stopTimes = new ArrayList<>();
        String[] next;

        try {
            CSVReader reader = new CSVReader(inputStream);
            while (true) {
                next = reader.readNext();
                if (next != null) {
                    if (!next[0].equals(BUS_TIMES_CHECK)) {
                        StopTime stopTime = new StopTime();

                        stopTime.tripID = Integer.parseInt(next[0]);
                        stopTime.arrivalTime = next[1];
                        stopTime.departureTime = next[2];
                        stopTime.stopID = next[3];
                        stopTime.stopSequence = Integer.parseInt(next[4]);

                        stopTimes.add(stopTime);
                    }
                }
                else {
                    break;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return stopTimes;
    }
}

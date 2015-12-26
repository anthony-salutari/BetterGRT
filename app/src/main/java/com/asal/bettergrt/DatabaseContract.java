package com.asal.bettergrt;

import android.provider.BaseColumns;

/**
 * Created by Anthony on 12/26/2015.
 */
public final class DatabaseContract {

    // empty constructor
    public DatabaseContract() {
    }

    public static abstract class BusStopEntry implements BaseColumns {
        public static final String BUS_STOPS_TABLE_NAME = "bus_stops";
        public static final String COLUMN_NAME_STOP_LAT = "stopLat";
        public static final String COLUMN_NAME_STOP_LON = "stopLon";
        public static final String COLUMN_NAME_STOP_ID = "stopID";
        public static final String COLUMN_NAME_STOP_NAME = "stopName";
        public static final String COLUMN_NAME_STOP_LOCATION = "location";
    }

    public static abstract class BusRouteEntry implements BaseColumns {
        public static final String BUS_ROUTES_TABLE_NAME = "bus_routes";
        public static final String COLUMN_NAME_ROUTE_LONG_NAME = "routeLongName";
        public static final String COLUMN_NAME_ROUTE_ID = "routeID";
        public static final String COLUMN_NAME_ROUTE_TYPE = "routeType";
        public static final String COLUMN_NAME_ROUTE_SHORT_NAME = "routeShortName";
    }

    public static abstract class BusTripEntry implements BaseColumns {
        public static final String BUS_TRIPS_TABLE_NAME = "bus_trips";
        public static final String COLUMN_NAME_BLOCK_ID = "blockID";
        public static final String COLUMN_NAME_ROUTE_ID = "routeID";
        public static final String COLUMN_NAME_DIRECTION_ID = "directionID";
        public static final String COLUMN_NAME_TRIP_HEADSIGN = "tripHeadsign";
        public static final String COLUMN_NAME_SHAPE_ID = "shapeID";
        public static final String COLUMN_NAME_SERVICE_ID = "serviceID";
        public static final String COLUMN_NAME_TRIP_ID = "tripID";
    }

    public static abstract class StopTimeEntry implements BaseColumns {
        public static final String STOP_TIMES_TABLE_NAME = "stop_times";
        public static final String COLUMN_NAME_TRIP_ID = "tripID";
        public static final String COLUMN_NAME_ARRIVAL_TIME = "arrivalTime";
        public static final String COLUMN_NAME_DEPARTURE_TIME = "departureTime";
        public static final String COLUMN_NAME_STOP_ID = "stopID";
        public static final String COLUMN_NAME_STOP_SEQUENCE = "stopSequence";
    }
}

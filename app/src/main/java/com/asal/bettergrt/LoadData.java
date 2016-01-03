package com.asal.bettergrt;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.opencsv.CSVReader;

import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Anthony on 12/23/2015.
 */
public class LoadData {

    private DatabaseHelper mDbHelper;

    // static final strings to compare for the CSV reader to skip the first row
    private static final String BUS_ROUTE_CHECK = "route_long_name";
    private static final String BUS_STOP_CHECK = "stop_lat";
    private static final String BUS_TRIP_CHECK = "block_id";
    private static final String BUS_TIMES_CHECK = "trip_id";

    public LoadData(Context context, final InputStreamReader[] streamReaders) {
        mDbHelper = new DatabaseHelper(context);

        // thread for loading stop times
        new Thread(new Runnable() {
            @Override
            public void run() {
                loadStopTimes(streamReaders[0]);
            }
        }).start();

        // thread for loading bus routes
        new Thread(new Runnable() {
            @Override
            public void run() {
                loadBusRoutes(streamReaders[1]);
            }
        }).start();

        // thread for loading bus stops
        new Thread(new Runnable() {
            @Override
            public void run() {
                loadBusStops(streamReaders[2]);
            }
        }).start();

        // thread for loading bus trips
        new Thread(new Runnable() {
            @Override
            public void run() {
                loadBusTrips(streamReaders[3]);
            }
        }).start();
    }

    public void loadStopTimes(InputStreamReader inputStream) {
        ArrayList<ContentValues> values = new ArrayList<>();
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String[] next;

        try {
            CSVReader reader = new CSVReader(inputStream);
            while (true) {
                next = reader.readNext();
                if (next != null) {
                    if (!next[0].equals(BUS_TIMES_CHECK)) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(DatabaseContract.StopTimeEntry.COLUMN_NAME_STOPTIME_TRIP_ID, Integer.parseInt(next[0]));
                        contentValues.put(DatabaseContract.StopTimeEntry.COLUMN_NAME_STOPTIME_ARRIVAL_TIME, next[1]);
                        contentValues.put(DatabaseContract.StopTimeEntry.COLUMN_NAME_STOPTIME_DEPARTURE_TIME, next[2]);
                        contentValues.put(DatabaseContract.StopTimeEntry.COLUMN_NAME_STOPTIME_STOP_ID, next[3]);
                        contentValues.put(DatabaseContract.StopTimeEntry.COLUMN_NAME_STOPTIME_STOP_SEQUENCE, Integer.parseInt(next[4]));
                        values.add(contentValues);
                    }
                }
                else {
                    db.beginTransaction();
                    for (ContentValues value : values) {
                        db.insert(DatabaseContract.StopTimeEntry.STOP_TIMES_TABLE_NAME, null, value);
                    }
                    db.setTransactionSuccessful();
                    db.endTransaction();
                    break;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void loadBusRoutes(InputStreamReader inputStream) {
        ArrayList<ContentValues> values = new ArrayList<>();
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String[] next;

        // read the routes.txt file and put into
        try {
            CSVReader reader = new CSVReader(inputStream);
            while (true) {
                next = reader.readNext();
                if (next != null) {
                    if (!next[0].equals(BUS_ROUTE_CHECK)) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(DatabaseContract.BusRouteEntry.COLUMN_NAME_ROUTE_LONG_NAME, next[0]);
                        contentValues.put(DatabaseContract.BusRouteEntry.COLUMN_NAME_ROUTE_ID, Integer.parseInt(next[1]));
                        contentValues.put(DatabaseContract.BusRouteEntry.COLUMN_NAME_ROUTE_TYPE, Integer.parseInt(next[2]));
                        contentValues.put(DatabaseContract.BusRouteEntry.COLUMN_NAME_ROUTE_SHORT_NAME, Integer.parseInt(next[8]));
                        values.add(contentValues);
                    }
                }
                else {
                    db.beginTransaction();
                    for (ContentValues value : values) {
                        db.insert(DatabaseContract.BusRouteEntry.BUS_ROUTES_TABLE_NAME, null, value);
                    }
                    db.setTransactionSuccessful();
                    db.endTransaction();
                    break;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void loadBusStops(InputStreamReader inputStream) {
        ArrayList<ContentValues> values = new ArrayList<>();
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String[] next;

        try {
            CSVReader reader = new CSVReader(inputStream);
            while (true) {
                next = reader.readNext();
                if (next != null) {
                    if(!next[0].equals(BUS_STOP_CHECK)) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(DatabaseContract.BusStopEntry.COLUMN_NAME_STOPS_STOP_LAT, Double.parseDouble(next[0]));
                        contentValues.put(DatabaseContract.BusStopEntry.COLUMN_NAME_STOPS_STOP_LON, Double.parseDouble(next[2]));
                        contentValues.put(DatabaseContract.BusStopEntry.COLUMN_NAME_STOPS_STOP_ID, next[3]);
                        contentValues.put(DatabaseContract.BusStopEntry.COLUMN_NAME_STOPS_STOP_NAME, next[7]);
                        values.add(contentValues);
                    }
                }
                else {
                    db.beginTransaction();
                    for (ContentValues value : values) {
                        db.insert(DatabaseContract.BusStopEntry.BUS_STOPS_TABLE_NAME, null, value);
                    }
                    db.setTransactionSuccessful();
                    db.endTransaction();
                    break;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void loadBusTrips(InputStreamReader inputStream) {
        ArrayList<ContentValues> values = new ArrayList<>();
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String[] next;

        try {
            CSVReader reader = new CSVReader(inputStream);
            while (true) {
                next = reader.readNext();
                if (next != null) {
                    if (!next[0].equals(BUS_TRIP_CHECK)) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(DatabaseContract.BusTripEntry.COLUMN_NAME_TRIP_BLOCK_ID, next[0]);
                        contentValues.put(DatabaseContract.BusTripEntry.COLUMN_NAME_TRIP_ROUTE_ID, Integer.parseInt(next[1]));
                        contentValues.put(DatabaseContract.BusTripEntry.COLUMN_NAME_TRIP_DIRECTION_ID, Integer.parseInt(next[2]));
                        contentValues.put(DatabaseContract.BusTripEntry.COLUMN_NAME_TRIP_HEADSIGN, next[3]);
                        contentValues.put(DatabaseContract.BusTripEntry.COLUMN_NAME_TRIP_SHAPE_ID, next[4]);
                        contentValues.put(DatabaseContract.BusTripEntry.COLUMN_NAME_TRIP_SERVICE_ID, next[5]);
                        contentValues.put(DatabaseContract.BusTripEntry.COLUMN_NAME_TRIP_ID, Integer.parseInt(next[6]));
                        values.add(contentValues);
                    }
                }
                else {
                    db.beginTransaction();
                    for (ContentValues value : values) {
                        db.insert(DatabaseContract.BusTripEntry.BUS_TRIPS_TABLE_NAME, null, value);
                    }
                    db.setTransactionSuccessful();
                    db.endTransaction();
                    break;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}

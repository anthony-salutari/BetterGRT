package com.asal.bettergrt;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Anthony on 12/26/2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "bettergrt_db";
    private static final int DATABASE_VERSION = 1;
    private SQLiteDatabase mDatabase;

    // table create statements
    private static final String CREATE_TABLE_BUS_STOPS = "CREATE TABLE " + DatabaseContract.BusStopEntry.BUS_STOPS_TABLE_NAME + " (" +
            DatabaseContract.BusStopEntry._ID + " INTEGER PRIMARY KEY," +
            DatabaseContract.BusStopEntry.COLUMN_NAME_STOPS_STOP_LAT + " REAL," +
            DatabaseContract.BusStopEntry.COLUMN_NAME_STOPS_STOP_LON + " REAL," +
            DatabaseContract.BusStopEntry.COLUMN_NAME_STOPS_STOP_ID + " INTEGER," +
            DatabaseContract.BusStopEntry.COLUMN_NAME_STOPS_STOP_NAME + " TEXT)";
    private static final String CREATE_TABLE_BUS_ROUTES = "CREATE TABLE " + DatabaseContract.BusRouteEntry.BUS_ROUTES_TABLE_NAME + " (" +
            DatabaseContract.BusRouteEntry._ID + " INTEGER PRIMARY KEY," +
            DatabaseContract.BusRouteEntry.COLUMN_NAME_ROUTE_LONG_NAME + " TEXT," +
            DatabaseContract.BusRouteEntry.COLUMN_NAME_ROUTE_ID + " INTEGER," +
            DatabaseContract.BusRouteEntry.COLUMN_NAME_ROUTE_TYPE + " INTEGER," +
            DatabaseContract.BusRouteEntry.COLUMN_NAME_ROUTE_SHORT_NAME + " INTEGER)";
    private static final String CREATE_TABLE_BUS_TRIPS = "CREATE TABLE " + DatabaseContract.BusTripEntry.BUS_TRIPS_TABLE_NAME + " (" +
            DatabaseContract.BusTripEntry._ID + " INTEGER PRIMARY KEY," +
            DatabaseContract.BusTripEntry.COLUMN_NAME_TRIP_BLOCK_ID + " TEXT," +
            DatabaseContract.BusTripEntry.COLUMN_NAME_TRIP_ROUTE_ID + " INTEGER," +
            DatabaseContract.BusTripEntry.COLUMN_NAME_TRIP_DIRECTION_ID + " INTEGER," +
            DatabaseContract.BusTripEntry.COLUMN_NAME_TRIP_HEADSIGN + " TEXT," +
            DatabaseContract.BusTripEntry.COLUMN_NAME_TRIP_SHAPE_ID + " TEXT," +
            DatabaseContract.BusTripEntry.COLUMN_NAME_TRIP_SERVICE_ID + " TEXT," +
            DatabaseContract.BusTripEntry.COLUMN_NAME_TRIP_ID + " INTEGER)";
    private static final String CREATE_TABLE_STOP_TIMES = "CREATE TABLE " + DatabaseContract.StopTimeEntry.STOP_TIMES_TABLE_NAME + " (" +
            DatabaseContract.StopTimeEntry._ID + " INTEGER PRIMARY KEY," +
            DatabaseContract.StopTimeEntry.COLUMN_NAME_STOPTIME_TRIP_ID + " INTEGER," +
            DatabaseContract.StopTimeEntry.COLUMN_NAME_STOPTIME_ARRIVAL_TIME + " TEXT," +
            DatabaseContract.StopTimeEntry.COLUMN_NAME_STOPTIME_DEPARTURE_TIME + " TEXT," +
            DatabaseContract.StopTimeEntry.COLUMN_NAME_STOPTIME_STOP_ID + " TEXT," +
            DatabaseContract.StopTimeEntry.COLUMN_NAME_STOPTIME_STOP_SEQUENCE + " INTEGER)";
    private static final String CREATE_TABLE_FAVOURITES = "CREATE TABLE " + DatabaseContract.FavouritesEntry.FAVOURITES_TABLE_NAME + " (" +
            DatabaseContract.FavouritesEntry._ID + " INTEGER PRIMARY KEY," +
            DatabaseContract.FavouritesEntry.COLUMN_NAME_FAVOURITES_STOP_NUMBER + " INTEGER," +
            DatabaseContract.FavouritesEntry.COLUMN_NAME_FAVOURITES_STOP_NAME + " TEXT," +
            DatabaseContract.FavouritesEntry.COLUMN_NAME_FAVOURITES_STOP_LAT + " REAL," +
            DatabaseContract.FavouritesEntry.COLUMN_NAME_FAVOURITES_STOP_LON + " REAL," +
            DatabaseContract.FavouritesEntry.COLUMN_NAME_FAVOURITES_NICKNAME + " TEXT)";

    // table delete statements
    private static final String DELETE_TABLE_BUS_STOPS = "DROP TABLE IF EXISTS " + DatabaseContract.BusStopEntry.BUS_STOPS_TABLE_NAME;
    private static final String DELETE_TABLE_BUS_ROUTES = "DROP TABLE IF EXISTS " + DatabaseContract.BusRouteEntry.BUS_ROUTES_TABLE_NAME;
    private static final String DELETE_TABLE_BUS_TRIPS = "DROP TABLE IF EXISTS " + DatabaseContract.BusTripEntry.BUS_TRIPS_TABLE_NAME;
    private static final String DELETE_TABLE_STOP_TIMES = "DROP TABLE IF EXISTS " + DatabaseContract.StopTimeEntry.STOP_TIMES_TABLE_NAME;
    private static final String DELETE_TABLE_FAVOURITES = "DROP TABLE IF EXISTS " + DatabaseContract.FavouritesEntry.FAVOURITES_TABLE_NAME;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_BUS_STOPS);
        db.execSQL(CREATE_TABLE_BUS_ROUTES);
        db.execSQL(CREATE_TABLE_BUS_TRIPS);
        db.execSQL(CREATE_TABLE_STOP_TIMES);
        db.execSQL(CREATE_TABLE_FAVOURITES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DELETE_TABLE_BUS_STOPS);
        db.execSQL(DELETE_TABLE_BUS_ROUTES);
        db.execSQL(DELETE_TABLE_BUS_TRIPS);
        db.execSQL(DELETE_TABLE_STOP_TIMES);
        db.execSQL(DELETE_TABLE_FAVOURITES);
        onCreate(db);
    }
}

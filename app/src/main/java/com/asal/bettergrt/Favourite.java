package com.asal.bettergrt;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Anthony on 12/24/2015.
 */
public class Favourite {

    private int stopNumber;
    private String stopName;
    private double stopLat;
    private double stopLon;
    private LatLng location;
    private String nickname;

    // default constructor
    public Favourite() {

    }

    // primary constructor
    public Favourite(int stopNumber, String stopName, LatLng location) {
        this.stopNumber = stopNumber;
        this.stopName = stopName;
        this.location = location;
        this.stopLat = location.latitude;
        this.stopLon = location.longitude;
    }

    // secondary constructor
    public Favourite(int stopNumber, String stopName, double stopLat, double stopLon) {
        this.stopNumber = stopNumber;
        this.stopName = stopName;
        this.stopLat = stopLat;
        this.stopLon = stopLon;
        this.location = new LatLng(stopLat, stopLon);
    }

    // setters
    public void setStopNumber(int stopNumber) {
        this.stopNumber = stopNumber;
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }

    public void setStopLat(double stopLat) {
        this.stopLat = stopLat;
    }

    public void setStopLon(double stopLon) {
        this.stopLon = stopLon;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public void setNickname(String nickname) {
        // ensure that nickname is less than a certain length
        this.nickname = nickname;
    }

    // getters
    public int getStopNumber() {
        return this.stopNumber;
    }

    public String getStopName() {
        return this.stopName;
    }

    public double getStopLat() {
        return this.stopLat;
    }

    public double getStopLon() {
        return this.stopLon;
    }

    public LatLng getLocation() {
        return this.location;
    }

    public String getNickname() {
        return this.nickname;
    }
}

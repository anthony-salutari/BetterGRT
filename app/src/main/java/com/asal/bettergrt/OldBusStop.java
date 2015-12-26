package com.asal.bettergrt;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by Anthony on 12/23/2015.
 */
public class OldBusStop implements ClusterItem{
    public LatLng location;
    public int id;
    public String name;

    public OldBusStop(double lat, double lng) {
        location = new LatLng(lat, lng);
    }

    public OldBusStop() {

    }

    // getters
    public LatLng getLocation() {
        return this.location;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    // setters
    public void setLocation(LatLng value) {
        this.location = value;
    }

    public void setId(int value) {
        this.id = value;
    }

    public void setName(String value) {
        this.name = value;
    }

    @Override
    public LatLng getPosition() {
        return location;
    }
}

package com.asal.bettergrt;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by Anthony on 12/24/2015.
 */
public class BusStop implements ClusterItem{
    public double stopLat;
    public double stopLon;
    public int stopID;
    public String stopName;
    public LatLng location;

    @Override
    public LatLng getPosition() {
        return location;
    }
}

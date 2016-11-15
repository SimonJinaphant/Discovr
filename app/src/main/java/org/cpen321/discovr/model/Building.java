package org.cpen321.discovr.model;

import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.List;

/**
 * Created by Simon Jinaphant on 09-Nov-2016.
 */

public class Building {
    public final String name;
    public final String code;
    public final String address;
    public final String hours;
    public final List<LatLng> coordinates;

    public Building(String name, String code, String address, String hours, List<LatLng> coordinates){
        this.name = name;
        this.code = code;
        this.address = address;
        this.hours = hours;
        this.coordinates = coordinates;
    }

}

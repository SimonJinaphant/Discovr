package org.cpen321.discovr.model;

import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.List;

/**
 * Model of a Bus/Train station to display on a map.
 */

public class MapTransitStation{

    // The transit station number to query via the Transit API.
    public final String stationNumber;

    // The Latitude/Longitude of the station on the map.
    public final LatLng location;

    // All vehicles numbers that stops at this station.
    public final List<String> vehicles;

    public MapTransitStation(String stationNumber, LatLng location, List<String> vehicles) {
        this.stationNumber = stationNumber;
        this.location = location;
        this.vehicles = vehicles;
    }

}

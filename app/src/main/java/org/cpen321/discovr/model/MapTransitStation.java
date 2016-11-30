package org.cpen321.discovr.model;

import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.List;

/**
 * Model of a Bus/Train station to display on a map.
 */

public class MapTransitStation {

    // Name of the transit station stop.
    public final String name;

    // The station ID number to query via the Transit API.
    public final String identifier;

    // The Latitude/Longitude of the station on the map.
    public final LatLng location;

    public MapTransitStation(String identifier, LatLng location, String name) {
        this.identifier = identifier;
        this.location = location;
        this.name = name;
    }

}

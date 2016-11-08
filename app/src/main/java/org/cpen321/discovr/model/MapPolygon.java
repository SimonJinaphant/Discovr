package org.cpen321.discovr.model;

import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.List;

/**
 * Model class of a drawable polygon on the MapboxMap
 */

public class MapPolygon {
    public final String name;
    public final List<LatLng> vertices;

    public MapPolygon(String name, List<LatLng> vertices){
        this.name = name;
        this.vertices = vertices;
    }
}

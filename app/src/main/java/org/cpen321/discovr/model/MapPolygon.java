package org.cpen321.discovr.model;

import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.List;

/**
 * Model class of a drawable polygon on the MapboxMap.
 * A polygon is meant to mark a general area that's not confined to one building.
 */

public class MapPolygon{
    // The name of the polygon area.
    public final String name;

    // Collection of Latitude/Longitude which forms the polygon vertices.
    public final List<LatLng> vertices;

    public MapPolygon(String name, List<LatLng> vertices){
        this.name = name;
        this.vertices = vertices;
    }
}

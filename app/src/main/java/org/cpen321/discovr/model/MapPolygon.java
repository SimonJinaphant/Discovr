package org.cpen321.discovr.model;

import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.List;

/**
 * Created by Simon Jinaphant on 08-Nov-2016.
 */

public class MapPolygon {
    public final String name;
    public final List<LatLng> vertices;

    public MapPolygon(String name, List<LatLng> vertices){
        this.name = name;
        this.vertices = vertices;
    }
}

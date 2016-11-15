package org.cpen321.discovr.model;


import android.util.Log;
import android.widget.LinearLayout;

import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Simon Jinaphant on 09-Nov-2016.
 */

public class Building {
    public String name;
    public String code;
    public String address;
    public String hours;
    private List<LatLng> coordinates = new ArrayList<LatLng>();

    public Building(String name, String code, String address, String hours, String coordinates){
        this.name = name;
        this.code = code;
        this.address = address;
        this.hours = hours;
        if(coordinates != null) {
            String[] stringLatLng = coordinates.split("%");

            for (int i = 0; i < stringLatLng.length; i++) {
                String[] temp = stringLatLng[i].split(",");
                Double latitude = Double.parseDouble(temp[0]);
                Double longtitude = Double.parseDouble(temp[1]);
                LatLng addL = new LatLng(latitude, longtitude);
                this.coordinates.add(addL);
            }
        }
    }

    public Building(String name, String code, String address, String hours, List<LatLng> coordinates){
        this.name = name;
        this.code = code;
        this.address = address;
        this.hours = hours;
        this.coordinates = coordinates;
    }


    public String getCoordinatesAsString(){
        String c = "";
        for (LatLng l : coordinates){
            c = c + String.valueOf(l.getLatitude()) + "," + String.valueOf(l.getLongitude()) + "%";

        }
        return c.substring(0, c.length()-1);
    }

    public List<LatLng> getAllCoordinates(){
        return this.coordinates;
    }
}

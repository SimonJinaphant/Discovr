package org.cpen321.discovr.model;

import android.widget.LinearLayout;

import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.List;

/**
 * Created by Simon Jinaphant on 09-Nov-2016.
 */

public class Building {
    public String name;
    public String code;
    public String address;
    public String hours;
    public List<LatLng> coordinates;

    public Building(String name, String code, String address, String hours, String coordinates){
        this.name = name;
        this.code = code;
        this.address = address;
        this.hours = hours;
        String[] stringLatLng = coordinates.split("%^");
        for (String l : stringLatLng){
            String[] temp = l.split(",");
            double latitude = Double.parseDouble(temp[0]);
            double longtitude = double.parseDouble(temp[1]);
            LatLng addL = new LatLng(latitude, longtitude);
            this.coordinates.add(addL);

        }
    }

    public String getCoordinates(){
        String c = "";
        for (LatLng l : coordinates){
            c = c + l.toString() + "%^";
        }
    }

}

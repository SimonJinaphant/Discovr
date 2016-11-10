package org.cpen321.discovr.model;

/**
 * Created by David Wong on 2016-11-08.
 */

public class Building {

    public final String name;
    public final String address;
    public final String code;
    public final String hours;
    public final double coordinates[];


    public Building (String name, String address, String code, String hours, double coordinates[]){

        this.name = name;
        this.address = address;
        this.code = code;
        this.hours = hours;
        this.coordinates = coordinates;
    }

}

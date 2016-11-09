package org.cpen321.discovr.model;

/**
 * Created by jacqu on 11/9/2016.
 */

public class BuildingInformation {
    private String name;
    private String code;
    private String address;
    private String hours;
    private String coordinates;

    BuildingInformation(String name, String code, String address, String hours, String coordinates){
        this.name = name;
        this.code = code;
        this.address = address;
        this.hours = hours;
        this.coordinates = coordinates;
    }

    public String getName(){
        return this.name;
    }

    public String getCode(){
        return this.code;
    }
    public String getAddress(){
        return this.address;
    }
    public String getHours(){
        return this.hours;
    }

    public String getCoordinates(){
        return this.coordinates;
    }
}

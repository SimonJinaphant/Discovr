package org.cpen321.discovr;

import java.util.ArrayList;

import static java.lang.Integer.parseInt;

/**
 * Created by jacqueline on 10/28/2016.
 */

public class EventInfo {
    private int id;
    private String name;
    private String buildingName;
    private String time;
    private String location;
    private String eventDetails;
    private String[] tags;
    private int size;

    EventInfo(String id, String newInfo){
        this.id = parseInt(id);
        String[] splitInfo = newInfo.split("%");

        setID(Integer.valueOf(splitInfo[0]));
        setName(splitInfo[1]);
        setBuildingName(splitInfo[2]);
        setLocation(splitInfo[3]);
        setTime(splitInfo[4]);
        setEventDetails(splitInfo[5]);
        for(int i = 6; i < splitInfo.length; i++){
            addTag(splitInfo[i]);
        }


        size = this.tags.length;
    }

    void setID( int id ){
        this.id = id;
    }

    void setName( String name ){
        this.name = name;
    }

    void setBuildingName( String buildingName ){
        this.buildingName = buildingName;
    }

    void setTime( String time ){
        this.time = time;
    }

    void setLocation( String location ){
        this.location = location;
    }

    void setEventDetails( String eventDetails ){
        this.eventDetails = eventDetails;
    }

    void addTag( String tag ) {
        this.tags[size] = tag;
        size++;
    }

    int getID (){
        return this.id;
    }

    String getName (){return this.name; }

    String getBuildingName(){ return this.buildingName;}

    String getTime(){ return this.time;}

    String getLocation(){ return this.location;}

    String getEventDetails(){ return this.eventDetails; }

    String[] getTags(){
        return this.tags;
    }

    String getInfoString(){
        String infoString = getID() + "%" + getName() + "%" + getBuildingName() + "%" + getLocation() + "%" + getTime() + "%" + getEventDetails();
        for (String s:tags){
            infoString = "%" + s;
        }

        return infoString;
    }



}

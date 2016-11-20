package org.cpen321.discovr.model;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static java.lang.Integer.parseInt;

/**
 * Created by jacqueline on 10/28/2016.
 */

public class EventInfo {
    private int id;
    private String name;
    private String hostName;
    private String buildingName;
    private Date startTime;
    private Date endTime;
    private String eventDetails;

    public EventInfo(int id, String name, String hostName, String buildingName, String startTime, String endTime, String eventDetails){
        setID(id);
        setName(name);
        setHostName(hostName);
        setBuildingName(buildingName);
        setTime(startTime, endTime);
        setEventDetails(eventDetails);
    }


    public void setID( int id ){
        this.id = id;
    }

    public void setName( String name ){
        this.name = name;
    }

    public void setHostName( String hostName ){
        this.hostName = hostName;
    }

    public void setBuildingName( String buildingName ){
        this.buildingName = buildingName;
    }

    public void setTime( String startTime, String endTime ){
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'kk:mm:ss", Locale.CANADA);
        try {
            this.startTime = format.parse(startTime);
            this.endTime = format.parse(endTime);
        } catch (ParseException e){
            e.printStackTrace();
        }
    }

    public void setEventDetails( String eventDetails ){
        this.eventDetails = eventDetails;
    }

    public int getID (){
        return this.id;
    }

    public String getName (){return this.name; }

    public String getHostName(){return this.hostName; }

    public String getBuildingName(){ return this.buildingName;}

    public Date getStartTime(){return this.startTime;}

    public String getStartTimeString(){
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'kk:mm:ss", Locale.CANADA);
        Log.d("eventDate", "Start Time: " + format.format(this.startTime));
        return format.format(this.startTime);
    }

    public String getEndTimeString(){
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'kk:mm:ss", Locale.CANADA);
        Log.d("eventDate", "End Time: " + format.format(this.endTime));
        return format.format(this.endTime);
    }

    public Date getEndTime(){ return this.endTime;}

    public String getEventDetails(){ return this.eventDetails; }

    public static String getDateString(Date Time){
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CANADA);
        return format.format(Time);
    }

    public static String getTimeString(Date Time){
        DateFormat format = new SimpleDateFormat("kk:mm:ss", Locale.CANADA);
        return format.format(Time);
    }


    /*String getTags(){
        String allTags = "";
        for (String s:this.tags){
            allTags = s + "%";
        }
        return allTags;
    }*/

    /*
    Used to test Sqlite when there were two columns ID | Rest of Event Details
    String getInfoString(){
        String infoString = getID() + "%" + getName() + "%" + getBuildingName() + "%" + getLocation() + "%" + getTime() + "%" + getEventDetails();
        for (String s:tags){
            infoString = "%" + s;
        }

        return infoString;
    }
    */



}

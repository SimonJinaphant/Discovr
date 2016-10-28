package org.cpen321.discovr;

import java.util.ArrayList;

import static java.lang.Integer.parseInt;

/**
 * Created by jacqueline on 10/28/2016.
 */

public class EventInfo {
    private int id;
    private String[] info;
    int size = 0;

    EventInfo(String id, String newInfo){
        this.id = parseInt(id);
        this.info = newInfo.split("%");
        size = this.info.length;
    }

    void setID( int id ){
        this.id = id;
    }

    void addTag( String info ){
        this.info[size] = info;
        size++;
    }

    int getID (){
        return this.id;
    }
    String[] getInfo(){
        return this.info;
    }

    String getInfoString(){
        String infoString = "";
        for (String s:info){
            infoString = info+s+"%";
        }

        return infoString;
    }

}

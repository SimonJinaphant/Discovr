package org.cpen321.discovr;

import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import static org.junit.Assert.*;

public class EventClientManagerTest {

    EventClientManager ecm;

    @Before
    public void initialize(){
        ecm = new EventClientManager(){
            @Override
            public void updateEventsList() {

            }
        };
    }

    @Test
    public void addOneDayTest(){
        String dateString = "18-11-2016 00:00:00";
        String dateStringTmrw = "19-11-2016 00:00:00";
        SimpleDateFormat format = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");

        try{
            Date midnightNov18 = format.parse(dateString);
            Date midnightNov19 = format.parse(dateStringTmrw);
            midnightNov18 = ecm.addOneDay(midnightNov18);
            assertEquals(midnightNov18, midnightNov19);
        } catch (ParseException e){
            e.printStackTrace();
        }
    }

}
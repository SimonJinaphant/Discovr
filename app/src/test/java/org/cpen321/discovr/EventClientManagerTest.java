package org.cpen321.discovr;

import org.cpen321.discovr.model.EventInfo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import static org.junit.Assert.*;

public class EventClientManagerTest {

    EventClientManager ecm;
    List<EventInfo> testEventList;
    int[] eventID = {0,1,2,3,4,5};
    String[] eventName = {"Event 1", "Event 2", "Event 3", "Event 4", "Event 5", "Event 6"};
    String[] hostName = {"Host 1", "Host 2", "Host 3", "Host 4", "Host 5", "Host 6"};
    String[] loc = {"Loc 1", "Loc 2", "Loc 3", "Loc 4", "Loc 5", "Loc 6"};
    String[] startTime = {"2016-11-21T08:00:00", "2016-11-21T09:00:00", "2016-11-22T10:00:00", "2016-11-22T11:00:00", "2016-11-24T12:00:00", "2016-11-21T12:00:00"};
    String[] endTime = {"2016-11-21T09:00:00", "2016-11-22T10:00:00", "2016-11-22T11:00:00", "2016-11-25T12:00:00", "2016-11-24T13:00:00", "2016-11-26T12:00:00"};
    String[] description = {"Description for event 1", "Description for event 2", "Description for event 3", "Description for event 4", "Description for event 5", "Description for event 6"};

    @Before
    public void setUp(){
        testEventList = new ArrayList<>();

        // List of Test Events
        for (int i = 0; i < eventID.length; i++){
            testEventList.add(new EventInfo(eventID[i], eventName[i], hostName[i], loc[i], startTime[i], endTime[i], description[i]));
        }

        ecm = new EventClientManager(testEventList){
            @Override
            public void updateEventsList() {

            }
        };


        //randomize the event order
        Collections.shuffle(testEventList);
    }

    @After
    public void tearDown(){
        ecm = null;
        testEventList.clear();
    }

   @Test
   public void testEventDateFilter(){
       Calendar cal = Calendar.getInstance();
       cal.set(2016, Calendar.NOVEMBER, 22, 0, 0, 0);
       Date dayAfter = ecm.addOneDay(cal.getTime());
       ecm.eventDateFilter(testEventList, cal.getTime(), dayAfter);

       EventInfo e0 = new EventInfo(eventID[0], eventName[0], hostName[0], loc[0], startTime[0], endTime[0], description[0]);
       EventInfo e1 = new EventInfo(eventID[1], eventName[1], hostName[1], loc[1], startTime[1], endTime[1], description[1]);
       EventInfo e2 = new EventInfo(eventID[2], eventName[2], hostName[2], loc[2], startTime[2], endTime[2], description[2]);
       EventInfo e3 = new EventInfo(eventID[3], eventName[3], hostName[3], loc[3], startTime[3], endTime[3], description[3]);
       EventInfo e4 = new EventInfo(eventID[4], eventName[4], hostName[4], loc[4], startTime[4], endTime[4], description[4]);
       EventInfo e5 = new EventInfo(eventID[5], eventName[5], hostName[5], loc[5], startTime[5], endTime[5], description[5]);

       assertFalse(testEventList.contains(e0));
       assertFalse(testEventList.contains(e4));
       assertTrue(testEventList.contains(e1));
       assertTrue(testEventList.contains(e2));
       assertTrue(testEventList.contains(e3));
       assertTrue(testEventList.contains(e5));
   }

    @Test
    public void testFindEvent(){
        EventInfo event = new EventInfo(eventID[5], eventName[5], hostName[5], loc[5], startTime[5], endTime[5], description[5]);
        EventInfo foundEvent = ecm.findEvent(eventID[5]);
        assertEquals(event, foundEvent);
    }

    @Test
    public void testSortList(){
        ecm.sortList(testEventList);
        for (int i = 0; i < eventID.length - 1; i++){
            assertTrue(testEventList.get(i).getStartTime().before(testEventList.get(i+1).getStartTime()));
        }

    }

    @Test
    public void testAddOneDay(){
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
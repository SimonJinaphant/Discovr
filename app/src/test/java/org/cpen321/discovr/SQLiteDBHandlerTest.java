package org.cpen321.discovr;

import org.cpen321.discovr.model.EventInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import static org.junit.Assert.*;

/**
 * Created by lerir on 2016-11-21.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk=23)
public class SQLiteDBHandlerTest {

    int[] eventID = {0,1,2,3,4,5};
    String[] eventName = {"Event 1", "Event 2", "Event 3", "Event 4", "Event 5", "Event 6"};
    String[] hostName = {"Host 1", "Host 2", "Host 3", "Host 4", "Host 5", "Host 6"};
    String[] loc = {"Loc 1", "Loc 2", "Loc 3", "Loc 4", "Loc 5", "Loc 6"};
    String[] startTime = {"2016-11-21T08:00:00", "2016-11-21T09:00:00", "2016-11-22T10:00:00", "2016-11-22T11:00:00", "2016-11-24T12:00:00", "2016-11-21T12:00:00"};
    String[] endTime = {"2016-11-21T09:00:00", "2016-11-22T10:00:00", "2016-11-22T11:00:00", "2016-11-25T12:00:00", "2016-11-24T13:00:00", "2016-11-26T12:00:00"};
    String[] description = {"Description for event 1", "Description for event 2", "Description for event 3", "Description for event 4", "Description for event 5", "Description for event 6"};

    SQLiteDBHandler dbh;

    List<EventInfo> events;

    @Before
    public void setUp(){
        events = new ArrayList<>();
        dbh = new SQLiteDBHandler(RuntimeEnvironment.application);
        for (int i = 0; i < eventID.length; i++){
            events.add(new EventInfo(eventID[i], eventName[i], hostName[i], loc[i], startTime[i], endTime[i], description[i]));
        }

    }

    @Test
    public void testExistence(){
        assertNotNull(dbh);
    }


    @Test
    public void testEventCount(){
        assertEquals(0, dbh.getEventCount());
    }

    @Test
    public void testAddingEvent(){
        assertEquals(0, dbh.getEventCount());
        dbh.addEvent(events.get(0));
        assertEquals(1, dbh.getEventCount());
    }

    @Test
    public void testGetEventByID(){
        addEventsToDatabase();
        EventInfo with_id_2 = dbh.getEvent(eventID[2]);
        assertEquals(events.get(2), with_id_2);
    }

    // TODO: Expand to search by Location and Host
    @Test
    public void testGetEventBySearch(){
        addEventsToDatabase();
        List<EventInfo> events = dbh.getEventbySearch("event");
        assertEquals(eventID.length, events.size());
        List<EventInfo> event1 = dbh.getEventbySearch(eventName[1]);
        assertEquals(events.get(1), event1.get(0));
    }

    @Test
    public void testGetAllEvents(){
        addEventsToDatabase();
        List<EventInfo> allEvents = dbh.getAllEvents();
        assertEquals(eventID.length, allEvents.size());
        for (int i = 0; i < allEvents.size(); i++){
            assertTrue(allEvents.contains(events.get(i)));
        }
    }

    @Test
    public void getUpdateEvent(){
        addEventsToDatabase();
        EventInfo updateEvent = new EventInfo(eventID[4], eventName[4], "New Updated Host", loc[4], startTime[4], endTime[4], description[4]);
        dbh.updateEvent(updateEvent);
        List<EventInfo> allEvents = dbh.getEventbySearch(eventName[4]);
        assertEquals(1, allEvents.size());
        assertEquals(updateEvent, allEvents.get(0));
    }

    @Test
    public void testDeleteEvent(){
        addEventsToDatabase();
        dbh.deleteEvent(eventID[5]);
        assertEquals(eventID.length - 1, dbh.getEventCount());
        List<EventInfo> deletedEvent = dbh.getEventbySearch(eventName[5]);
        assertEquals(0, deletedEvent.size());
    }

    @Test
    public void testAddCourses(){

    }

    @Test
    public void testGetAllCourses(){

    }

    @Test
    public void testAddBuilding(){

    }

    @Test
    public void testGetBuildingByCode(){

    }

    @Test
    public void testGetBuildingCount(){

    }

    @Test
    public void testGetBuildingByID(){

    }

    @Test
    public void testGetAllBuildings(){

    }

    @Test
    public void deleteEvent(){
        addEventsToDatabase();

    }

    public void addEventsToDatabase(){
        dbh.addEvent(events.get(0));
        dbh.addEvent(events.get(1));
        dbh.addEvent(events.get(2));
        dbh.addEvent(events.get(3));
        dbh.addEvent(events.get(4));
        dbh.addEvent(events.get(5));
    }


}